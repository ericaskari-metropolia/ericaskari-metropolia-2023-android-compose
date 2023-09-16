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
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservice.IBluetoothDeviceServiceRepository
import com.ericaskari.w4d5bluetooth.bluetoothsearch.IBluetoothDeviceRepository
import com.ericaskari.w4d5bluetooth.enums.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


data class DeviceDescriptor(
    val uuid: String,
    val name: String,
    val charUuid: String,
    val permissions: List<CharacteristicDescriptorPermission>,
    val notificationProperty: CharacteristicProperty?,
    val readBytes: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceDescriptor

        if (uuid != other.uuid) return false
        if (name != other.name) return false
        if (charUuid != other.charUuid) return false
        if (permissions != other.permissions) return false
        if (notificationProperty != other.notificationProperty) return false
        if (readBytes != null) {
            if (other.readBytes == null) return false
            if (!readBytes.contentEquals(other.readBytes)) return false
        } else if (other.readBytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + charUuid.hashCode()
        result = 31 * result + permissions.hashCode()
        result = 31 * result + (notificationProperty?.hashCode() ?: 0)
        result = 31 * result + (readBytes?.contentHashCode() ?: 0)
        return result
    }
}


data class DeviceCharacteristics(
    val uuid: String,
    val name: String,
    val descriptor: String?,
    val permissions: Int,
    val properties: List<CharacteristicProperty>,
    val writeTypes: List<CharacteristicWriteType>,
    val descriptors: List<DeviceDescriptor>,
    val canRead: Boolean,
    val canWrite: Boolean,
    val readBytes: ByteArray?,
    val notificationBytes: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceCharacteristics

        if (uuid != other.uuid) return false
        if (name != other.name) return false
        if (descriptor != other.descriptor) return false
        if (permissions != other.permissions) return false
        if (properties != other.properties) return false
        if (writeTypes != other.writeTypes) return false
        if (descriptors != other.descriptors) return false
        if (canRead != other.canRead) return false
        if (canWrite != other.canWrite) return false
        if (readBytes != null) {
            if (other.readBytes == null) return false
            if (!readBytes.contentEquals(other.readBytes)) return false
        } else if (other.readBytes != null) return false
        if (notificationBytes != null) {
            if (other.notificationBytes == null) return false
            if (!notificationBytes.contentEquals(other.notificationBytes)) return false
        } else if (other.notificationBytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (descriptor?.hashCode() ?: 0)
        result = 31 * result + permissions
        result = 31 * result + properties.hashCode()
        result = 31 * result + writeTypes.hashCode()
        result = 31 * result + descriptors.hashCode()
        result = 31 * result + canRead.hashCode()
        result = 31 * result + canWrite.hashCode()
        result = 31 * result + (readBytes?.contentHashCode() ?: 0)
        result = 31 * result + (notificationBytes?.contentHashCode() ?: 0)
        return result
    }
}

data class DeviceService(
    val uuid: String,
    val name: String,
    val characteristics: List<DeviceCharacteristics>
)


class AppBluetoothGattService(
    private val bluetoothDeviceRepository: IBluetoothDeviceRepository,
    private val bluetoothDeviceServiceRepository: IBluetoothDeviceServiceRepository,
    private val btAdapter: BluetoothAdapter,
    private val scope: CoroutineScope,
    private val app: Application,
) {
    private var btGatt: BluetoothGatt? = null

    val connectMessage = MutableStateFlow(ConnectionState.DISCONNECTED)
    val deviceDetails = MutableStateFlow<List<DeviceService>>(emptyList())

    private val bluetoothGattCallback = appBluetoothGattCallbackFactory(
        scope = scope,
        onConnectionStateChange = { gatt, connectionState ->
            btGatt = gatt
            connectMessage.value = connectionState
            println("connectionState: $connectionState")
        },
        onServicesDiscovered = {
            it.entries.forEach { serviceAndCharacteristicsMap ->
                val service = serviceAndCharacteristicsMap.key
                val characteristicsMap = serviceAndCharacteristicsMap.value

                println("Service:         ${service.uuid}")

                characteristicsMap.entries.forEach { characteristicsAndInfoMap ->
                    val characteristic = characteristicsAndInfoMap.key
                    val characteristicInfoMap = characteristicsAndInfoMap.value

                    val permissions = characteristicInfoMap.permissions
                    val properties = characteristicInfoMap.properties

                    println("characteristics: ${characteristic.uuid} permissions: ${permissions} properties: ${properties}")

                    characteristicInfoMap.descriptors.entries.forEach { characteristicAndDescriptorMap ->
                        val descriptor = characteristicAndDescriptorMap.key
                        val descriptorPermissionList = characteristicAndDescriptorMap.value

                        println("descriptor:      ${descriptor.uuid} descriptorPermissionList: ${descriptorPermissionList}")

                    }

                }
            }

            scope.launch {
                enableNotificationsAndIndications()
            }
        }
    )

    suspend fun enableNotificationsAndIndications() {
        println("[AppBluetoothGattService] enableNotificationsAndIndications")

        btGatt?.services?.forEach { gattSvcForNotify ->
            gattSvcForNotify.characteristics?.forEach { svcChar ->

//                svcChar.descriptors.find { desc ->
//                    desc.uuid.toString() == CCCD.uuid
//                }?.also { cccd ->
////                    val notifyRegistered = btGatt?.setCharacteristicNotification(svcChar, true)
//
//                    if (svcChar.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
//                        cccd.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
////                        btGatt?.writeDescriptor(cccd)
//                    }
//
//                    if (svcChar.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
//                        cccd.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)
////                        btGatt?.writeDescriptor(cccd)
//                    }
//
//                    // give gatt a little breathing room for writes
//                    delay(300L)
//
//                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(address: String) {
        println("[AppBluetoothGattService] connect")
        if (!btAdapter.isEnabled) {
            println("[AppBluetoothGattService] connect btAdapter.isEnabled is false")
            return
        }

        try {
            connectMessage.value = ConnectionState.CONNECTING
            val device = btAdapter.getRemoteDevice(address)
            println("[AppBluetoothGattService] connect $device")
            device.connectGatt(app, false, bluetoothGattCallback)
        } catch (e: Exception) {
            connectMessage.value = ConnectionState.DISCONNECTED
            println(e)
        }
    }

    fun readCharacteristic(uuid: String) {
        println("[AppBluetoothGattService] connect")
        btGatt?.services?.flatMap { it.characteristics }?.find { svcChar ->
            svcChar.uuid.toString() == uuid
        }?.also { foundChar ->
            println("Found Char: " + foundChar.uuid.toString())
//            btGatt?.readCharacteristic(foundChar)
        }
    }

    fun readDescriptor(charUuid: String, descUuid: String) {

        val currentCharacteristic = btGatt?.services?.flatMap { it.characteristics }?.find { char ->
            char.uuid.toString() == charUuid
        }
        currentCharacteristic?.let { char ->
            char.descriptors.find { desc ->
                desc.uuid.toString() == descUuid
            }?.also { foundDesc ->
                println("Found Char: $charUuid; " + foundDesc.uuid.toString())
                //btGatt?.readDescriptor(foundDesc)
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
        deviceDetails.value = emptyList()
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
                    println("[AppBluetoothGattService][bluetoothGattCallback] onConnectionStateChange")

                    val connectionState = ConnectionState.fromBluetoothProfileState(newState)

                    onConnectionStateChange(gatt, connectionState)

                    if (connectionState == ConnectionState.CONNECTED) {
                        gatt.discoverServices()
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                    super.onServicesDiscovered(gatt, status)
                    println("[AppBluetoothGattService][bluetoothGattCallback] onServicesDiscovered: status: $status")
                    if (gatt == null) {
                        println("[AppBluetoothGattService][bluetoothGattCallback] onServicesDiscovered: gatt is null")
                        return
                    }
                    scope.launch {
                        println(gatt)

                        val output: Map<BluetoothGattService, Map<BluetoothGattCharacteristic, CharacteristicsInfo>> =
                            gatt.services.fold(mutableMapOf()) servicesFold@{ servicesMap, service ->
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

                        onServicesDiscovered(output)
                    }
                }

                override fun onCharacteristicRead(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    value: ByteArray,
                    status: Int
                ) {
                    super.onCharacteristicRead(gatt, characteristic, value, status)
                    println("[AppBluetoothGattService][bluetoothGattCallback] onCharacteristicRead")
//            deviceDetails.value = parseRead(deviceDetails.value, characteristic, status)
                }

                @Deprecated("Deprecated in Java")
                override fun onCharacteristicRead(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    status: Int
                ) {
                    super.onCharacteristicRead(gatt, characteristic, status)
                    println("[AppBluetoothGattService][bluetoothGattCallback] onCharacteristicRead")
//            deviceDetails.value = parseRead(deviceDetails.value, characteristic, status)
                }

                @Deprecated("Deprecated in Java")
                override fun onCharacteristicChanged(
                    gatt: BluetoothGatt?,
                    characteristic: BluetoothGattCharacteristic
                ) {
                    super.onCharacteristicChanged(gatt, characteristic)
                    println("[AppBluetoothGattService][bluetoothGattCallback] onCharacteristicChanged")
                    println("characteristic changed: ${characteristic.value}")
//            deviceDetails.value = parseNotification(deviceDetails.value, characteristic, characteristic.value)
                }

                override fun onCharacteristicChanged(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    value: ByteArray
                ) {
                    super.onCharacteristicChanged(gatt, characteristic, value)
                    println("[AppBluetoothGattService][bluetoothGattCallback] onCharacteristicChanged")
                    println("characteristic changed: ${value}")
//            deviceDetails.value = parseNotification(deviceDetails.value, characteristic, value)
                }

                @Deprecated("Deprecated in Java")
                override fun onDescriptorRead(
                    gatt: BluetoothGatt?,
                    descriptor: BluetoothGattDescriptor,
                    status: Int
                ) {
                    super.onDescriptorRead(gatt, descriptor, status)
                    println("[AppBluetoothGattService][bluetoothGattCallback] onDescriptorRead")

                    println(
                        "descriptor read: ${descriptor.uuid}, " +
                                "${descriptor.characteristic.uuid}, $status, ${descriptor.value}"
                    )

//            deviceDetails.value = parseDescriptor(deviceDetails.value, descriptor, status, descriptor.value)
                }

                override fun onDescriptorRead(
                    gatt: BluetoothGatt,
                    descriptor: BluetoothGattDescriptor,
                    status: Int,
                    value: ByteArray
                ) {
                    super.onDescriptorRead(gatt, descriptor, status, value)
                    println("[AppBluetoothGattService][bluetoothGattCallback] onDescriptorRead")

                    println(
                        "descriptor read: ${descriptor.uuid}, " +
                                "${descriptor.characteristic.uuid}, $status, ${value}"
                    )

//            deviceDetails.value = parseDescriptor(deviceDetails.value, descriptor, status, value)
                }

                override fun onCharacteristicWrite(
                    gatt: BluetoothGatt?,
                    characteristic: BluetoothGattCharacteristic,
                    status: Int
                ) {
                    super.onCharacteristicWrite(gatt, characteristic, status)
                    println("[AppBluetoothGattService][bluetoothGattCallback] onCharacteristicWrite")
//            btGatt?.readCharacteristic(characteristic)
                }

                override fun onDescriptorWrite(
                    gatt: BluetoothGatt?,
                    descriptor: BluetoothGattDescriptor,
                    status: Int
                ) {
                    super.onDescriptorWrite(gatt, descriptor, status)
                    println("[AppBluetoothGattService][bluetoothGattCallback] onCharacteristicWrite")
                    println("descriptor write: ${descriptor.uuid}, ${descriptor.characteristic.uuid}, $status")
                }

            }
        }
    }
}