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
import com.ericaskari.w4d5bluetooth.bluetooth.models.CharacteristicDescriptorPermission
import com.ericaskari.w4d5bluetooth.bluetooth.models.CharacteristicProperty
import com.ericaskari.w4d5bluetooth.bluetooth.models.CharacteristicWriteType
import com.ericaskari.w4d5bluetooth.bluetooth.models.CharacteristicsInfo
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
        scope = scope,
        onConnectionStateChange = { gatt, connectionState ->
            val prefix = "[AppBluetoothGattService][onConnectionStateChange]"
            println(prefix)
            btGatt = gatt
            connectMessage.value = connectionState
            println("$prefix connectionState: $connectionState")
        },
        onServicesDiscovered = {
            val prefix = "[AppBluetoothGattService][onServicesDiscovered]"
            println(prefix)

            scope.launch {
                val serviceList = it.keys.toList().map { BluetoothDeviceService.fromBluetoothGattService(it, btGatt!!.device.address) }

                bluetoothDeviceServiceRepository.syncItems(*serviceList.toTypedArray())

                val characteristicList = btGatt!!.services!!.flatMap { service -> service.characteristics }.map { characteristic ->
                    BluetoothDeviceServiceCharacteristic.fromBluetoothGattCharacteristic(
                        characteristic,
                        characteristic.service.uuid.toString()
                    )
                }

                bluetoothDeviceServiceCharacteristicRepository.syncItems(*characteristicList.toTypedArray())

                val descriptorList = btGatt!!.services!!
                    .flatMap { service -> service.characteristics }
                    .flatMap { characteristic -> characteristic.descriptors }
                    .map { descriptor ->
                        BluetoothDeviceServiceCharacteristicDescriptor.fromBluetoothGattDescriptor(
                            descriptor,
                            descriptor.characteristic.uuid.toString()
                        )
                    }


                bluetoothDeviceServiceCharacteristicDescriptorRepository.syncItems(*descriptorList.toTypedArray())
            }

            it.entries.forEach { serviceAndCharacteristicsMap ->
                val service = serviceAndCharacteristicsMap.key
                val characteristicsMap = serviceAndCharacteristicsMap.value


                println("$prefix Service:         ${service.uuid}")

                characteristicsMap.entries.forEach { characteristicsAndInfoMap ->
                    val characteristic = characteristicsAndInfoMap.key
                    val characteristicInfoMap = characteristicsAndInfoMap.value

                    val permissions = characteristicInfoMap.permissions
                    val properties = characteristicInfoMap.properties


                    println("$prefix characteristics: ${characteristic.uuid} permissions[${characteristic.permissions}]: $permissions properties[${characteristic.properties}]: $properties")
                    characteristicInfoMap.descriptors.entries.forEach { characteristicAndDescriptorMap ->
                        val descriptor = characteristicAndDescriptorMap.key
                        val descriptorPermissionList = characteristicAndDescriptorMap.value

                        println("$prefix descriptor:      ${descriptor.uuid} permissions[${descriptor.permissions}]: $descriptorPermissionList")

                    }

                }
            }

            scope.launch {
                enableNotificationsAndIndications()
            }
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
        val normalized = normalizeBluetoothGattServices(btGatt!!.services)
        normalized.entries.forEach { serviceAndCharacteristicsMap ->
            val service = serviceAndCharacteristicsMap.key
            val characteristicsMap = serviceAndCharacteristicsMap.value

            characteristicsMap.entries.forEach { characteristicsAndInfoMap ->
                val characteristic = characteristicsAndInfoMap.key
                val characteristicInfoMap = characteristicsAndInfoMap.value

                val permissions = characteristicInfoMap.permissions
                val properties = characteristicInfoMap.properties

                val notifyRegistered = btGatt!!.setCharacteristicNotification(characteristic, true)

                println("$prefix characteristics: ${characteristic.uuid} notifyRegistered: $notifyRegistered")

                delay(3000)
                characteristicInfoMap.descriptors.entries.forEach { characteristicAndDescriptorMap ->
                    val descriptor = characteristicAndDescriptorMap.key
                    val descriptorPermissionList = characteristicAndDescriptorMap.value

                    if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                        println("$prefix descriptor: ${descriptor.uuid} setValue: ENABLE_NOTIFICATION_VALUE}")
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                        btGatt!!.writeDescriptor(descriptor)
                    }

                    if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
                        println("$prefix descriptor: ${descriptor.uuid} setValue: ENABLE_INDICATION_VALUE")
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)
                        btGatt!!.writeDescriptor(descriptor)
                    }

                    delay(3000)

                }

            }
        }


        //        btGatt?.services?.forEach { gattSvcForNotify ->
        //            gattSvcForNotify.characteristics?.forEach { svcChar ->
        //                svcChar.descriptors.find { desc ->
        //                    desc.uuid.toString() == CCCD.uuid
        //                }?.also { cccd ->
        ////                    val notifyRegistered = btGatt?.setCharacteristicNotification(svcChar, true)
        //
        //                    if (svcChar.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
        //                        cccd.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        //                        btGatt?.writeDescriptor(cccd)
        //                    }
        //
        //                    if (svcChar.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
        //                        cccd.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)
        //                        btGatt?.writeDescriptor(cccd)
        //                    }
        //
        //                    // give gatt a little breathing room for writes
        //                    delay(300L)
        //
        //                }
        //            }
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

    suspend fun readAll() {
        val prefix = "[AppBluetoothGattService][readAll]"
        println(prefix)

        if (btGatt == null) {
            println("$prefix Not connected")
            return
        }

        val output = normalizeBluetoothGattServices(btGatt!!.services)

        output.entries.forEach { serviceAndCharacteristicsMap ->
            val service = serviceAndCharacteristicsMap.key
            val characteristicsMap = serviceAndCharacteristicsMap.value

            println("$prefix Service:         ${service.uuid}")

            characteristicsMap.entries.forEach { characteristicsAndInfoMap ->
                val characteristic = characteristicsAndInfoMap.key
                val characteristicInfoMap = characteristicsAndInfoMap.value

                val permissions = characteristicInfoMap.permissions
                val properties = characteristicInfoMap.properties

                println("$prefix characteristics: ${characteristic.uuid} permissions: ${permissions} properties: ${properties}")
                readCharacteristic(characteristic.uuid.toString())
                delay(1000)
                characteristicInfoMap.descriptors.entries.forEach { characteristicAndDescriptorMap ->
                    val descriptor = characteristicAndDescriptorMap.key
                    val descriptorPermissionList = characteristicAndDescriptorMap.value

                    println("$prefix descriptor:      ${descriptor.uuid} descriptorPermissionList: ${descriptorPermissionList}")
                    readDescriptor(characteristic.uuid.toString(), descriptor.uuid.toString())
                    delay(1000)

                }

            }
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

    fun close() {
        connectMessage.value = ConnectionState.DISCONNECTED
        try {
            btGatt?.let { gatt ->
//                gatt.disconnect()
//                gatt.close()
                btGatt = null
            }
        } catch (e: Exception) {
//            Timber.tag("BTGATT_CLOSE").e(e)
        } finally {
            btGatt = null
        }
    }

    companion object {


        private fun appBluetoothGattCallbackFactory(
            scope: CoroutineScope,
            onConnectionStateChange: (gatt: BluetoothGatt, connectionState: ConnectionState) -> Unit,
            onServicesDiscovered: (result: Map<BluetoothGattService, Map<BluetoothGattCharacteristic, CharacteristicsInfo>>) -> Unit,
        ): BluetoothGattCallback {

            return object : BluetoothGattCallback() {

                @SuppressLint("MissingPermission")
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    super.onConnectionStateChange(gatt, status, newState)
                    val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onConnectionStateChange]"
                    println(prefix)

                    val connectionState = ConnectionState.fromBluetoothProfileState(newState)
                    println("$prefix connectionState: $connectionState")

                    onConnectionStateChange(gatt, connectionState)

                    if (connectionState == ConnectionState.CONNECTED) {
                        gatt.discoverServices()
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                    super.onServicesDiscovered(gatt, status)
                    val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onServicesDiscovered]"
                    println(prefix)
                    println("$prefix status: $status")
                    if (gatt == null) {
                        println("$prefix gatt is null")
                        return
                    }
                    scope.launch {
                        onServicesDiscovered(normalizeBluetoothGattServices(gatt.services))
                    }
                }

                override fun onCharacteristicRead(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    value: ByteArray,
                    status: Int
                ) {
                    super.onCharacteristicRead(gatt, characteristic, value, status)
                    val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onCharacteristicRead][4]"
                    println(prefix)
                    println("$prefix ${characteristic.uuid}, $status, ${value.print()}")
                    // deviceDetails.value = parseRead(deviceDetails.value, characteristic, status)
                }

                @Deprecated("Deprecated in Java")
                override fun onCharacteristicRead(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    status: Int
                ) {
                    super.onCharacteristicRead(gatt, characteristic, status)
                    val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onCharacteristicRead][3]"
                    println(prefix)
                    println("$prefix ${characteristic.uuid}, $status")
                    println("$prefix decodeToString          ${characteristic.value.decodeToString()}")
                    println("$prefix toHex                   ${characteristic.value.toHex()}")
                    println("$prefix decodeSkipUnreadable    ${characteristic.value.decodeSkipUnreadable("$prefix ")}")
                    println("$prefix print                   ${characteristic.value.print()}")
                    println("$prefix bitsToHex               ${characteristic.value.bitsToHex("$prefix  ")}")
                    println("$prefix bits                    ${characteristic.value.bits()}")
                    println("$prefix toBinaryString          ${characteristic.value.toBinaryString()}")
                }

                @Deprecated("Deprecated in Java")
                override fun onCharacteristicChanged(
                    gatt: BluetoothGatt?,
                    characteristic: BluetoothGattCharacteristic
                ) {
                    super.onCharacteristicChanged(gatt, characteristic)
                    val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onCharacteristicChanged][2]"
                    println(prefix)
                    println("$prefix ${characteristic.value.print()}")
                }

                override fun onCharacteristicChanged(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    value: ByteArray
                ) {
                    super.onCharacteristicChanged(gatt, characteristic, value)
                    val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onCharacteristicChanged][3]"
                    println(prefix)
                    println("$prefix ${value.print()}")
                    // deviceDetails.value = parseNotification(deviceDetails.value, characteristic, value)
                }

                @Deprecated("Deprecated in Java")
                override fun onDescriptorRead(
                    gatt: BluetoothGatt?,
                    descriptor: BluetoothGattDescriptor,
                    status: Int
                ) {
                    super.onDescriptorRead(gatt, descriptor, status)
                    val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onDescriptorRead][3]"
                    println(prefix)
                    println("$prefix descriptor read:        ${descriptor.uuid}, ${descriptor.characteristic.uuid}, $status")
                    println("$prefix descriptor read:        ${descriptor.uuid}, $status")
                    println("$prefix decodeToString          ${descriptor.value.decodeToString()}")
                    println("$prefix toHex                   ${descriptor.value.toHex()}")
                    println("$prefix decodeSkipUnreadable    ${descriptor.value.decodeSkipUnreadable("$prefix ")}")
                    println("$prefix print                   ${descriptor.value.print()}")
                    println("$prefix bitsToHex               ${descriptor.value.bitsToHex("$prefix ")}")
                    println("$prefix bits                    ${descriptor.value.bits()}")
                    println("$prefix toBinaryString          ${descriptor.value.toBinaryString()}")

                    // deviceDetails.value = parseDescriptor(deviceDetails.value, descriptor, status, descriptor.value)
                }

                override fun onDescriptorRead(
                    gatt: BluetoothGatt,
                    descriptor: BluetoothGattDescriptor,
                    status: Int,
                    value: ByteArray
                ) {
                    super.onDescriptorRead(gatt, descriptor, status, value)
                    val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onDescriptorRead][4]"
                    println(prefix)

                    println("$prefix ${descriptor.uuid}, " + "${descriptor.characteristic.uuid}, $status, ${value.print()}")

                    // deviceDetails.value = parseDescriptor(deviceDetails.value, descriptor, status, value)
                }

                @SuppressLint("MissingPermission")
                override fun onCharacteristicWrite(
                    gatt: BluetoothGatt?,
                    characteristic: BluetoothGattCharacteristic,
                    status: Int
                ) {
                    super.onCharacteristicWrite(gatt, characteristic, status)
                    val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onCharacteristicWrite][3]"
                    println(prefix)
                    gatt?.readCharacteristic(characteristic)
                }

                @SuppressLint("MissingPermission")
                override fun onDescriptorWrite(
                    gatt: BluetoothGatt?,
                    descriptor: BluetoothGattDescriptor,
                    status: Int
                ) {
                    super.onDescriptorWrite(gatt, descriptor, status)
                    val prefix = "[AppBluetoothGattService][bluetoothGattCallback][onDescriptorWrite][3]"
                    println(prefix)
                    println("$prefix ${descriptor.uuid}, ${descriptor.characteristic.uuid}, $status")
                    gatt?.readCharacteristic(descriptor.characteristic)
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
                            mutableMapOf<BluetoothGattDescriptor, List<CharacteristicDescriptorPermission>>()
                        ) descriptorFold@{ descriptorMap, descriptor ->

                            descriptorMap[descriptor] = CharacteristicDescriptorPermission.getAll(descriptor.permissions)
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