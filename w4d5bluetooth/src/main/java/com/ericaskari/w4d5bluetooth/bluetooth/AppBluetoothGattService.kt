package com.ericaskari.w4d5bluetooth.bluetooth

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.os.Build
import com.ericaskari.w4d5bluetooth.bluetooth.models.CharacteristicPermission
import com.ericaskari.w4d5bluetooth.bluetooth.models.CharacteristicProperty
import com.ericaskari.w4d5bluetooth.bluetooth.models.CharacteristicWriteType
import com.ericaskari.w4d5bluetooth.bluetooth.models.CharacteristicsInfo
import com.ericaskari.w4d5bluetooth.bluetooth.models.GattState
import com.ericaskari.w4d5bluetooth.bluetooth.models.bits
import com.ericaskari.w4d5bluetooth.bluetooth.models.bitsToHex
import com.ericaskari.w4d5bluetooth.bluetooth.models.decodeSkipUnreadable
import com.ericaskari.w4d5bluetooth.bluetooth.models.print
import com.ericaskari.w4d5bluetooth.bluetooth.models.toBinaryString
import com.ericaskari.w4d5bluetooth.bluetooth.models.toHex
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservice.BluetoothDeviceService
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservice.IBluetoothDeviceServiceRepository
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristic
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic.IBluetoothDeviceServiceCharacteristicRepository
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptor
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor.IBluetoothDeviceServiceCharacteristicDescriptorRepository
import com.ericaskari.w4d5bluetooth.bluetoothsearch.IBluetoothDeviceRepository
import com.ericaskari.w4d5bluetooth.enums.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID


class AppBluetoothGattService(
    private val bluetoothDeviceRepository: IBluetoothDeviceRepository,
    private val bluetoothDeviceServiceRepository: IBluetoothDeviceServiceRepository,
    private val bluetoothDeviceServiceCharacteristicRepository: IBluetoothDeviceServiceCharacteristicRepository,
    private val bluetoothDeviceServiceCharacteristicDescriptorRepository: IBluetoothDeviceServiceCharacteristicDescriptorRepository,
    private val btAdapter: BluetoothAdapter,
    private val scope: CoroutineScope,
    private val app: Application,
) {
    private var btGatt: BluetoothGatt? = null

    val connectMessage = MutableStateFlow(ConnectionState.DISCONNECTED)
    private val bluetoothGattCallback = appBluetoothGattCallbackFactory(
        onConnectionStateChange = { gatt, connectionState ->
            btGatt = gatt
            val prefix = "[AppBluetoothGattService][onConnectionStateChange]"
            println(prefix)
            connectMessage.value = connectionState
            println("$prefix connectionState: $connectionState")
        },
        onServicesDiscovered = { gatt ->
            btGatt = gatt
            val prefix = "[AppBluetoothGattService][onServicesDiscovered]"
            println(prefix)

            scope.launch {
                val serviceList = btGatt!!.services.map { BluetoothDeviceService.fromBluetoothGattService(it, btGatt!!.device.address) }

                bluetoothDeviceServiceRepository.syncItems(btGatt!!.device.address, *serviceList.toTypedArray())

                btGatt!!.services.forEach { service ->
                    val characteristics = service.characteristics.map characteristic@{ characteristic ->
                        return@characteristic BluetoothDeviceServiceCharacteristic.fromBluetoothGattCharacteristic(
                            characteristic,
                            characteristic.service.uuid.toString()
                        )
                    }

                    bluetoothDeviceServiceCharacteristicRepository.syncItems(service.uuid.toString(), *characteristics.toTypedArray())
                }

                btGatt!!.services.forEach { service ->
                    service.characteristics.forEach { characteristic ->
                        val descriptors = characteristic.descriptors.map descriptors@{ descriptor ->
                            return@descriptors BluetoothDeviceServiceCharacteristicDescriptor.fromBluetoothGattDescriptor(
                                descriptor.characteristic.uuid.toString(),
                                descriptor
                            )
                        }
                        bluetoothDeviceServiceCharacteristicDescriptorRepository.syncItems(
                            characteristic.uuid.toString(),
                            *descriptors.toTypedArray()
                        )
                    }

                }
            }

            btGatt!!.services.forEach { service ->
                println()
                println()
                println("$prefix Service:         ${service.uuid}")

                service.characteristics.forEach { characteristic ->
                    val permissions = characteristic.permissions
                    val properties = characteristic.properties
                    println()
                    println("$prefix characteristics: ${characteristic.uuid} permissions[${characteristic.permissions}]: $permissions properties[${characteristic.properties}]: $properties")

                    characteristic.descriptors.forEach { descriptor ->
                        val permissions = descriptor.permissions

                        println("$prefix descriptor:      ${descriptor.uuid} permissions[${descriptor.permissions}]: $permissions")

                    }

                }
            }

            scope.launch {
                enableNotificationsAndIndications()
            }
        },
        onCharacteristicChanged = { gatt, characteristic, value ->
            btGatt = gatt
            val prefix = "[AppBluetoothGattService][onCharacteristicChanged]"
            println(prefix)
            println("$prefix ${value.print()}")

            val bpm = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1)
            println("$prefix BPM: $bpm")
        },
        onCharacteristicRead = { gatt, characteristic, value, status ->
            val prefix = "[AppBluetoothGattService][onCharacteristicRead]"
            println(prefix)
            println("$prefix ${characteristic.uuid}, $status [${GattState.fromState(status)}] ${value.print()}")
            println("$prefix decodeToString          ${characteristic.value.decodeToString()}")
            println("$prefix toHex                   ${characteristic.value.toHex()}")
            println("$prefix decodeSkipUnreadable    ${characteristic.value.decodeSkipUnreadable("$prefix ")}")
            println("$prefix print                   ${characteristic.value.print()}")
            println("$prefix bitsToHex               ${characteristic.value.bitsToHex("$prefix  ")}")
            println("$prefix bits                    ${characteristic.value.bits()}")
            println("$prefix toBinaryString          ${characteristic.value.toBinaryString()}")
        },
        onDescriptorRead = { gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int, value: ByteArray ->

            val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onDescriptorRead][old]"
            println(prefix)
            println(
                "$prefix descriptor read:        ${descriptor.uuid}, ${descriptor.characteristic.uuid}, $status [${
                    GattState.fromState(
                        status
                    )
                }]"
            )
            println("$prefix descriptor read:        ${descriptor.uuid}, $status")
            println("$prefix decodeToString          ${descriptor.value.decodeToString()}")
            println("$prefix toHex                   ${descriptor.value.toHex()}")
            println("$prefix decodeSkipUnreadable    ${descriptor.value.decodeSkipUnreadable("$prefix ")}")
            println("$prefix print                   ${descriptor.value.print()}")
            println("$prefix bitsToHex               ${descriptor.value.bitsToHex("$prefix ")}")
            println("$prefix bits                    ${descriptor.value.bits()}")
            println("$prefix toBinaryString          ${descriptor.value.toBinaryString()}")

        },
        onCharacteristicWrite = { gatt, characteristic, status ->

            val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onCharacteristicWrite]"
            println(prefix)
            println("$prefix ${characteristic.uuid}, $status [${GattState.fromState(status).toString()}]")

        },
        onDescriptorWrite = { gatt, descriptor, status ->

            val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onDescriptorWrite]"
            println(prefix)
            println("$prefix ${descriptor.uuid}, $status [${GattState.fromState(status).toString()}]")

        }
    )

    @SuppressLint("MissingPermission")
    suspend fun enableNotificationsAndIndications() {
        val prefix = "[AppBluetoothGattService][enableNotificationsAndIndications]"
        println(prefix)

        if (btGatt == null) {
            println("$prefix btGatt is null.")
            return
        }

        btGatt!!.services.forEach { service ->

            service.characteristics.forEach { characteristic ->

                val configDescriptor = characteristic.descriptors.find {
                    it.uuid.toString().equals(CLIENT_CHARACTERISTIC_CONFIG_UUID, ignoreCase = true)
                }
                configDescriptor?.let {
                    val notifyRegistered = btGatt?.setCharacteristicNotification(characteristic, true)
                    println("$prefix notifyRegistered: $notifyRegistered")
                    if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        println("$prefix characteristic: ${characteristic.uuid} descriptor: ${it.uuid} >> ENABLE_NOTIFICATION_VALUE")

                        it.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                        btGatt?.writeDescriptor(it)
                    }

                    if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
                        println("$prefix characteristic: ${characteristic.uuid} descriptor: ${it.uuid} >> ENABLE_INDICATION_VALUE")
                        it.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)
                        btGatt?.writeDescriptor(it)
                    }
                    delay(300L)

                }
            }
        }

    }


    @SuppressLint("MissingPermission")
    suspend fun writeDescriptor(
        serviceId: String,
        characteristicId: String,
        descriptorId: String,
        value: ByteArray
    ) {
        val prefix = "[AppBluetoothGattService][writeDescriptor]"
        println(prefix)
        if (btGatt == null) {
            println("$prefix btGatt is null.")
            return
        }

        val queriedService = btGatt!!.getService(UUID.fromString(serviceId))
        if (queriedService == null) {
            println("$prefix service is null.")
            return
        }
        queriedService.let { service ->
            val queriedCharacteristic = service.getCharacteristic(UUID.fromString(characteristicId))
            if (queriedCharacteristic == null) {
                println("$prefix characteristic is null.")
                return
            }
            queriedCharacteristic.let { characteristic ->
                val queriedDescriptor = characteristic.getDescriptor(UUID.fromString(descriptorId))
                if (queriedDescriptor == null) {
                    println("$prefix queriedDescriptor is null.")
                    return
                }
                queriedDescriptor.let { descriptor ->
                    println("$prefix value is successfully set.")
                    descriptor.setValue(value)
                    btGatt!!.writeDescriptor(descriptor)
                    delay(300L)

                }

            }
        }
    }


    @SuppressLint("MissingPermission")
    suspend fun writeCharacteristic(
        serviceId: String,
        characteristicId: String,
        value: ByteArray
    ) {
        val prefix = "[AppBluetoothGattService][writeCharacteristic]"
        println(prefix)
        if (btGatt == null) {
            println("$prefix btGatt is null.")
            return
        }

        val queriedService = btGatt!!.getService(UUID.fromString(serviceId))
        if (queriedService == null) {
            println("$prefix service is null.")
            return
        }
        queriedService.let { service ->
            val queriedCharacteristic = service.getCharacteristic(UUID.fromString(characteristicId))
            if (queriedCharacteristic == null) {
                println("$prefix characteristic is null.")
                return
            }
            queriedCharacteristic.let { characteristic ->
                characteristic.setValue(value)
                btGatt!!.writeCharacteristic(characteristic)
                delay(300L)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(address: String) {
        val prefix = "[AppBluetoothGattService][connect]"
        println(prefix)

        if (!btAdapter.isEnabled) {
            println("$prefix btAdapter.isEnabled is false")
            return
        }

        try {
            connectMessage.value = ConnectionState.CONNECTING
            val device = btAdapter.getRemoteDevice(address)
            println("$prefix device: $device")
            device.connectGatt(app, false, bluetoothGattCallback)
        } catch (e: Exception) {
            connectMessage.value = ConnectionState.DISCONNECTED
            println(e)
        }
    }

    @SuppressLint("MissingPermission")
    fun readCharacteristic(uuid: String) {
        val prefix = "[AppBluetoothGattService][readCharacteristic]"
        println(prefix)

        btGatt?.services?.flatMap { it.characteristics }?.find { svcChar ->
            svcChar.uuid.toString() == uuid
        }?.also { foundChar ->
            btGatt?.readCharacteristic(foundChar)
        }
    }

    @SuppressLint("MissingPermission")
    fun readDescriptor(charUuid: String, descUuid: String) {
        val prefix = "[AppBluetoothGattService][readDescriptor]"
        println(prefix)

        val currentCharacteristic = btGatt?.services?.flatMap { it.characteristics }?.find { char ->
            char.uuid.toString() == charUuid
        }
        currentCharacteristic?.let { char ->
            char.descriptors.find { desc ->
                desc.uuid.toString() == descUuid
            }?.also { foundDesc ->
                btGatt?.readDescriptor(foundDesc)
            }
        }

    }

    fun writeBytes(uuid: String, bytes: ByteArray) {
        btGatt?.services?.flatMap { it.characteristics }?.find { svcChar ->
            svcChar.uuid.toString() == uuid
        }?.also { foundChar ->
            println("Found Char: " + foundChar.uuid.toString())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                btGatt?.writeCharacteristic(
//                    foundChar,
//                    bytes,
//                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
//                )
            } else {
                foundChar.setValue(bytes)
//                btGatt?.writeCharacteristic(foundChar)
            }
        }

    }

    fun writeDescriptor(charUuid: String, uuid: String, bytes: ByteArray) {
        btGatt?.services?.flatMap { it.characteristics }?.flatMap { it.descriptors }
            ?.find { it.characteristic.uuid.toString() == charUuid && it.uuid.toString() == uuid }
            ?.also { foundDescriptor ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    btGatt?.writeDescriptor(foundDescriptor, bytes)
                } else {
                    foundDescriptor.setValue(bytes)
//                    btGatt?.writeDescriptor(foundDescriptor)
                }
            }
    }

    @SuppressLint("MissingPermission")
    fun close() {
        try {
            btGatt?.let { gatt ->
                gatt.disconnect()
                gatt.close()
            }
        } catch (e: Exception) {
            println(e)
        } finally {
            btGatt = null
            connectMessage.value = ConnectionState.DISCONNECTED
        }
    }

    companion object {
        const val CLIENT_CHARACTERISTIC_CONFIG_UUID = "00002902-0000-1000-8000-00805f9b34fb"


        private fun appBluetoothGattCallbackFactory(
            onConnectionStateChange: (gatt: BluetoothGatt, connectionState: ConnectionState) -> Unit,
            onServicesDiscovered: (gatt: BluetoothGatt) -> Unit,
            onCharacteristicChanged: (gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) -> Unit,
            onCharacteristicRead: (gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray, status: Int) -> Unit,
            onDescriptorRead: (gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int, value: ByteArray) -> Unit,
            onCharacteristicWrite: (gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic, status: Int) -> Unit,
            onDescriptorWrite: (gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor, status: Int) -> Unit
        ): BluetoothGattCallback {

            return object : BluetoothGattCallback() {

                @SuppressLint("MissingPermission")
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    super.onConnectionStateChange(gatt, status, newState)
                    val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onConnectionStateChange]"
                    println(prefix)

                    val connectionState = ConnectionState.fromBluetoothProfileState(newState)

                    onConnectionStateChange(gatt, connectionState)

                    if (connectionState == ConnectionState.CONNECTED) {
                        gatt.discoverServices()
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    super.onServicesDiscovered(gatt, status)
                    onServicesDiscovered(gatt)
                }

                override fun onCharacteristicRead(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    value: ByteArray,
                    status: Int
                ) {
                    super.onCharacteristicRead(gatt, characteristic, value, status)
                    onCharacteristicRead(gatt, characteristic, value, status)
                }

                @Deprecated("Deprecated in Java")
                override fun onCharacteristicRead(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    status: Int
                ) {
                    super.onCharacteristicRead(gatt, characteristic, status)
                    onCharacteristicRead(gatt, characteristic, characteristic.value, status)
                }

                @Deprecated("Deprecated in Java")
                override fun onCharacteristicChanged(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic
                ) {
                    super.onCharacteristicChanged(gatt, characteristic)
                    onCharacteristicChanged(gatt, characteristic, characteristic.value)
                }

                override fun onCharacteristicChanged(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    value: ByteArray
                ) {
                    super.onCharacteristicChanged(gatt, characteristic, value)
                    onCharacteristicChanged(gatt, characteristic, value)
                }

                @Deprecated("Deprecated in Java")
                override fun onDescriptorRead(
                    gatt: BluetoothGatt,
                    descriptor: BluetoothGattDescriptor,
                    status: Int
                ) {
                    super.onDescriptorRead(gatt, descriptor, status)
                    onDescriptorRead(gatt, descriptor, status, descriptor.value)
                }

                override fun onDescriptorRead(
                    gatt: BluetoothGatt,
                    descriptor: BluetoothGattDescriptor,
                    status: Int,
                    value: ByteArray
                ) {
                    super.onDescriptorRead(gatt, descriptor, status, value)
                    onDescriptorRead(gatt, descriptor, status, value)
                }

                @SuppressLint("MissingPermission")
                override fun onCharacteristicWrite(
                    gatt: BluetoothGatt?,
                    characteristic: BluetoothGattCharacteristic,
                    status: Int
                ) {
                    super.onCharacteristicWrite(gatt, characteristic, status)
                    onCharacteristicWrite(gatt, characteristic, status)
                }

                @SuppressLint("MissingPermission")
                override fun onDescriptorWrite(
                    gatt: BluetoothGatt?,
                    descriptor: BluetoothGattDescriptor,
                    status: Int
                ) {
                    super.onDescriptorWrite(gatt, descriptor, status)
                    onDescriptorWrite(gatt, descriptor, status)
                }

            }
        }

        fun normalizeBluetoothGattServices(services: List<BluetoothGattService>): Map<BluetoothGattService, Map<BluetoothGattCharacteristic, CharacteristicsInfo>> {
            return services.fold(mutableMapOf()) servicesFold@{ servicesMap, service ->
                val characteristics = service.characteristics

                val infoListFolded: Map<BluetoothGattCharacteristic, CharacteristicsInfo> =
                    characteristics.fold(mutableMapOf()) characteristicsFold@{ mapVal, characteristic ->
                        val permissions = characteristic.permissions
                        val properties = CharacteristicProperty.getAll(characteristic.properties)
                        val writeTypes = CharacteristicWriteType.getAll(characteristic.writeType)

                        val descriptors = characteristic.descriptors.fold(
                            mutableMapOf<BluetoothGattDescriptor, List<CharacteristicPermission>>()
                        ) descriptorFold@{ descriptorMap, descriptor ->

                            descriptorMap[descriptor] = CharacteristicPermission.getAll(descriptor.permissions)
                            return@descriptorFold descriptorMap
                        }
                        val info = CharacteristicsInfo(
                            permissions = permissions,
                            properties = properties,
                            writeTypes = writeTypes,
                            descriptors = descriptors
                        )
                        mapVal[characteristic] = info

                        return@characteristicsFold mapVal
                    }

                servicesMap[service] = infoListFolded

                return@servicesFold servicesMap
            }
        }

    }
}