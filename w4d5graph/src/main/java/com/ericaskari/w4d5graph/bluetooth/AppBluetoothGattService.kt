package com.ericaskari.w4d5graph.bluetooth

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.os.Build
import com.ericaskari.w4d5graph.bluetooth.models.GattState
import com.ericaskari.w4d5graph.bluetooth.models.bits
import com.ericaskari.w4d5graph.bluetooth.models.bitsToHex
import com.ericaskari.w4d5graph.bluetooth.models.decodeSkipUnreadable
import com.ericaskari.w4d5graph.bluetooth.models.print
import com.ericaskari.w4d5graph.bluetooth.models.toBinaryString
import com.ericaskari.w4d5graph.bluetooth.models.toGss
import com.ericaskari.w4d5graph.bluetooth.models.toHex
import com.ericaskari.w4d5graph.bluetooth.models.toInteger
import com.ericaskari.w4d5graph.bluetoothdeviceservice.BluetoothDeviceService
import com.ericaskari.w4d5graph.bluetoothdeviceservice.IBluetoothDeviceServiceRepository
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristic
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristic.IBluetoothDeviceServiceCharacteristicRepository
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptor
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristicdescriptor.IBluetoothDeviceServiceCharacteristicDescriptorRepository
import com.ericaskari.w4d5graph.bluetoothdeviceservicevalue.BluetoothDeviceServiceValue
import com.ericaskari.w4d5graph.bluetoothdeviceservicevalue.IBluetoothDeviceServiceValueRepository
import com.ericaskari.w4d5graph.enums.ConnectionState
import com.ericaskari.w4d5graph.nordicsemiconductordatabase.IBluetoothServiceInfoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID


class AppBluetoothGattService(
    private val bluetoothServiceInfoRepository: IBluetoothServiceInfoRepository,
    private val bluetoothDeviceServiceRepository: IBluetoothDeviceServiceRepository,
    private val bluetoothDeviceServiceCharacteristicRepository: IBluetoothDeviceServiceCharacteristicRepository,
    private val bluetoothDeviceServiceCharacteristicDescriptorRepository: IBluetoothDeviceServiceCharacteristicDescriptorRepository,
    private val bluetoothDeviceServiceValueRepository: IBluetoothDeviceServiceValueRepository,
    private val btAdapter: BluetoothAdapter,
    private val scope: CoroutineScope,
    private val app: Application,
) {
    private var bluetoothGatt: BluetoothGatt? = null

    val connectMessage = MutableStateFlow(ConnectionState.DISCONNECTED)
    val output = MutableStateFlow<ByteArray?>(null)


    private val bluetoothGattCallback = appBluetoothGattCallbackFactory(
        onConnectionStateChange = { bluetoothGatt, connectionState ->
            this.bluetoothGatt = bluetoothGatt
            val prefix = "[AppBluetoothGattService][onConnectionStateChange]"
            println(prefix)
            connectMessage.value = connectionState
            println("$prefix connectionState: $connectionState")
        },
        onServicesDiscovered = { bluetoothGatt ->
            this.bluetoothGatt = bluetoothGatt
            val prefix = "[AppBluetoothGattService][onServicesDiscovered]"
            println(prefix)

            scope.launch {
                val infoList = bluetoothServiceInfoRepository.getAllItemsStream().first()
                val serviceList =
                    bluetoothGatt.services.map {
                        BluetoothDeviceService.fromBluetoothGattService(
                            it,
                            bluetoothGatt.device.address,
                            infoList
                        )
                    }

                bluetoothDeviceServiceRepository.syncItems(bluetoothGatt.device.address, *serviceList.toTypedArray())

                bluetoothGatt.services.forEach { service ->
                    val characteristics = service.characteristics.map characteristic@{ characteristic ->
                        return@characteristic BluetoothDeviceServiceCharacteristic.fromBluetoothGattCharacteristic(
                            characteristic,
                            serviceId = characteristic.service.uuid.toString(),
                            deviceId = bluetoothGatt.device.address.toString()
                        )
                    }

                    bluetoothDeviceServiceCharacteristicRepository.syncItems(service.uuid.toString(), *characteristics.toTypedArray())
                }

                bluetoothGatt!!.services.forEach { service ->
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

            bluetoothGatt!!.services.forEach { service ->
                println()
                println()
                println("$prefix Service:         ${service.uuid.toGss()}")

                service.characteristics.forEach { characteristic ->
                    val permissions = characteristic.permissions
                    val properties = characteristic.properties
                    println()
                    println("$prefix characteristics: ${characteristic.uuid.toGss()} permissions[${characteristic.permissions}]: $permissions properties[${characteristic.properties}]: $properties")

                    characteristic.descriptors.forEach { descriptor ->
                        val permissions = descriptor.permissions

                        println("$prefix descriptor:      ${descriptor.uuid.toGss()} permissions[${descriptor.permissions}]: $permissions")

                    }

                }
            }

        },
        onCharacteristicChanged = { bluetoothGatt, characteristic, value ->
            this.bluetoothGatt = bluetoothGatt
            output.value = value

            bluetoothDeviceServiceValueRepository.insertItem(
                BluetoothDeviceServiceValue.fromCharacteristic(
                    deviceId = bluetoothGatt.device.address,
                    characteristic = characteristic,
                    value = value
                )
            )
            val prefix = "[AppBluetoothGattService][onCharacteristicChanged]"

            println(prefix)
            println("$prefix decodeToString          ${value.decodeToString()}")
            println("$prefix toHex                   ${value.toHex()}")
            println("$prefix Integer                 ${value.toInteger()}")
            println("$prefix decodeSkipUnreadable    ${value.decodeSkipUnreadable("$prefix ")}")
            println("$prefix print                   ${value.print()}")
            println("$prefix bitsToHex               ${value.bitsToHex("$prefix  ")}")
            println("$prefix bits                    ${value.bits()}")
            println("$prefix toBinaryString          ${value.toBinaryString()}")
        },
        onCharacteristicRead = { bluetoothGatt, characteristic, value, status ->
            this.bluetoothGatt = bluetoothGatt
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
        onDescriptorRead = { bluetoothGatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int, value: ByteArray ->
            this.bluetoothGatt = bluetoothGatt

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
        onCharacteristicWrite = { bluetoothGatt, characteristic, status ->
            this.bluetoothGatt = bluetoothGatt

            val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onCharacteristicWrite]"
            println(prefix)
            println("$prefix ${characteristic.uuid}, $status [${GattState.fromState(status).toString()}]")

        },
        onDescriptorWrite = { bluetoothGatt, descriptor, status ->
            this.bluetoothGatt = bluetoothGatt

            val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onDescriptorWrite]"
            println(prefix)
            println("$prefix ${descriptor.uuid}, $status [${GattState.fromState(status).toString()}]")

        }
    )

    @SuppressLint("MissingPermission")
    suspend fun enableNotificationsAndIndications(
        deviceId: String,
        serviceId: String,
        characteristicId: String,
    ) {
        val prefix = "[AppBluetoothGattService][enableNotificationsAndIndications]"
        println(prefix)

        if (bluetoothGatt == null) {
            println("$prefix bluetoothGatt is null.")
            return
        }
        if (!bluetoothGatt?.device?.address.equals(deviceId, ignoreCase = true)) {
            println("$prefix Wrong Device.")
            return
        }

        val queriedService = bluetoothGatt!!.services.find { it.uuid.toString().equals(serviceId, ignoreCase = true) }

        queriedService?.let { service ->

            val queriedCharacteristics = service.characteristics.find { it.uuid.toString().equals(characteristicId, ignoreCase = true) }

            queriedCharacteristics?.let { characteristic ->
                val configDescriptor = characteristic.descriptors.find {
                    it.uuid.toString().equals(CLIENT_CHARACTERISTIC_CONFIG_UUID, ignoreCase = true)
                }
                configDescriptor?.let {
                    val notifyRegistered = bluetoothGatt?.setCharacteristicNotification(characteristic, true)
                    println("$prefix notifyRegistered: $notifyRegistered")
                    if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        println("$prefix characteristic: ${characteristic.uuid} descriptor: ${it.uuid} >> ENABLE_NOTIFICATION_VALUE")

                        it.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                        bluetoothGatt?.writeDescriptor(it)
                    }

                    if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
                        println("$prefix characteristic: ${characteristic.uuid} descriptor: ${it.uuid} >> ENABLE_INDICATION_VALUE")
                        it.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)
                        bluetoothGatt?.writeDescriptor(it)
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
        if (bluetoothGatt == null) {
            println("$prefix btGatt is null.")
            return
        }

        val queriedService = bluetoothGatt!!.getService(UUID.fromString(serviceId))
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
                    bluetoothGatt!!.writeDescriptor(descriptor)
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
        if (bluetoothGatt == null) {
            println("$prefix btGatt is null.")
            return
        }

        val queriedService = bluetoothGatt!!.getService(UUID.fromString(serviceId))
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
                bluetoothGatt!!.writeCharacteristic(characteristic)
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

        bluetoothGatt?.services?.flatMap { it.characteristics }?.find { svcChar ->
            svcChar.uuid.toString() == uuid
        }?.also { foundChar ->
            bluetoothGatt?.readCharacteristic(foundChar)
        }
    }

    @SuppressLint("MissingPermission")
    fun readDescriptor(charUuid: String, descUuid: String) {
        val prefix = "[AppBluetoothGattService][readDescriptor]"
        println(prefix)

        val currentCharacteristic = bluetoothGatt?.services?.flatMap { it.characteristics }?.find { char ->
            char.uuid.toString() == charUuid
        }
        currentCharacteristic?.let { char ->
            char.descriptors.find { desc ->
                desc.uuid.toString() == descUuid
            }?.also { foundDesc ->
                bluetoothGatt?.readDescriptor(foundDesc)
            }
        }

    }

    fun writeBytes(uuid: String, bytes: ByteArray) {
        bluetoothGatt?.services?.flatMap { it.characteristics }?.find { svcChar ->
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

    @SuppressLint("MissingPermission")
    fun writeDescriptor(charUuid: String, uuid: String, bytes: ByteArray) {
        bluetoothGatt?.services?.flatMap { it.characteristics }?.flatMap { it.descriptors }
            ?.find { it.characteristic.uuid.toString() == charUuid && it.uuid.toString() == uuid }
            ?.also { foundDescriptor ->
                foundDescriptor.setValue(bytes)
                bluetoothGatt?.writeDescriptor(foundDescriptor)
            }
    }

    @SuppressLint("MissingPermission")
    fun close() {
        try {
            bluetoothGatt?.let { gatt ->
                gatt.disconnect()
                gatt.close()
            }
        } catch (e: Exception) {
            println(e)
        } finally {
            bluetoothGatt = null
            connectMessage.value = ConnectionState.DISCONNECTED
            output.value = null
        }
    }

    companion object {
        const val CLIENT_CHARACTERISTIC_CONFIG_UUID = "00002902-0000-1000-8000-00805f9b34fb"


        private fun appBluetoothGattCallbackFactory(
            onConnectionStateChange: (bluetoothGatt: BluetoothGatt, connectionState: ConnectionState) -> Unit,
            onServicesDiscovered: (bluetoothGatt: BluetoothGatt) -> Unit,
            onCharacteristicChanged: (bluetoothGatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) -> Unit,
            onCharacteristicRead: (bluetoothGatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray, status: Int) -> Unit,
            onDescriptorRead: (bluetoothGatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int, value: ByteArray) -> Unit,
            onCharacteristicWrite: (bluetoothGatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic, status: Int) -> Unit,
            onDescriptorWrite: (bluetoothGatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor, status: Int) -> Unit
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
    }
}