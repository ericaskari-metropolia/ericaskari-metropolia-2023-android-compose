package com.ericaskari.w3d5beacon.bluetooth

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.os.Build
import com.ericaskari.w3d5beacon.enums.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


@SuppressLint("MissingPermission")
class AppBluetoothGatt
    (
    private val app: Application,
    private val btAdapter: BluetoothAdapter,
    private val scope: CoroutineScope,
) {

    private var btGatt: BluetoothGatt? = null

    val connectMessage = MutableStateFlow(ConnectionState.DISCONNECTED)

    private val bluetoothGattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            println("[AppBluetoothGatt] onConnectionStateChange")

            btGatt = gatt
            println("status: $status")

            when (newState) {
                BluetoothProfile.STATE_CONNECTING -> connectMessage.value =
                    ConnectionState.CONNECTING

                BluetoothProfile.STATE_CONNECTED -> {
                    connectMessage.value = ConnectionState.CONNECTED
                    btGatt?.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTING -> connectMessage.value =
                    ConnectionState.DISCONNECTING

                BluetoothProfile.STATE_DISCONNECTED -> connectMessage.value =
                    ConnectionState.DISCONNECTED

                else -> connectMessage.value = ConnectionState.DISCONNECTED
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            println("[AppBluetoothGatt] onServicesDiscovered")
            super.onServicesDiscovered(gatt, status)

            scope.launch {
                gatt?.let {
                    // deviceDetails.value = parseService(it, status)
                    // enableNotificationsAndIndications()
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, value, status)
            println("[AppBluetoothGatt] onCharacteristicRead")
//            deviceDetails.value = parseRead(deviceDetails.value, characteristic, status)
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            println("[AppBluetoothGatt] onCharacteristicRead")
//            deviceDetails.value = parseRead(deviceDetails.value, characteristic, status)
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            println("[AppBluetoothGatt] onCharacteristicChanged")
            println("characteristic changed:")
//            deviceDetails.value = parseNotification(deviceDetails.value, characteristic, characteristic.value)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            println("[AppBluetoothGatt] onCharacteristicChanged")
            println("characteristic changed: ")
//            deviceDetails.value = parseNotification(deviceDetails.value, characteristic, value)
        }

        @Deprecated("Deprecated in Java")
        override fun onDescriptorRead(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            super.onDescriptorRead(gatt, descriptor, status)
            println("[AppBluetoothGatt] onDescriptorRead")

            println(
                "descriptor read: ${descriptor.uuid}, " +
                        "${descriptor.characteristic.uuid}, $status"
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
            println("[AppBluetoothGatt] onDescriptorRead")

            // println("descriptor read: ${descriptor.uuid},  ${descriptor.characteristic.uuid}, $status, ${value.print()}")
            println("descriptor read: ${descriptor.uuid},  ${descriptor.characteristic.uuid}, $status")

            // deviceDetails.value = parseDescriptor(deviceDetails.value, descriptor, status, value)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            println("[AppBluetoothGatt] onCharacteristicWrite")
            btGatt?.readCharacteristic(characteristic)
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            println("[AppBluetoothGatt] onDescriptorWrite")
            println("descriptor write: ${descriptor.uuid}, ${descriptor.characteristic.uuid}, $status")
        }

    }

    fun connect(address: String) {
        println("[AppBluetoothGatt] connect")
        if (btAdapter.isEnabled) {
            btAdapter.let { adapter ->
                try {
                    connectMessage.value = ConnectionState.CONNECTING
                    val device = adapter.getRemoteDevice(address)
                    device.connectGatt(app, false, bluetoothGattCallback)
                } catch (e: Exception) {
                    connectMessage.value = ConnectionState.DISCONNECTED
                    println("BTGATT_CONNECT")
                    println(e)
                }
            }
        }
    }

    fun readCharacteristic(uuid: String) {
        println("[AppBluetoothGatt] readCharacteristic")
        btGatt?.services?.flatMap { it.characteristics }?.find { svcChar ->
            svcChar.uuid.toString() == uuid
        }?.also { foundChar ->
            println("Found Char: " + foundChar.uuid.toString())
            btGatt?.readCharacteristic(foundChar)
        }
    }

    fun readDescriptor(charUuid: String, descUuid: String) {
        println("[AppBluetoothGatt] readDescriptor")

        val currentCharacteristic = btGatt?.services?.flatMap { it.characteristics }?.find { char ->
            char.uuid.toString() == charUuid
        }
        currentCharacteristic?.let { char ->
            char.descriptors.find { desc ->
                desc.uuid.toString() == descUuid
            }?.also { foundDesc ->
                println("Found Char: $charUuid; " + foundDesc.uuid.toString())
                btGatt?.readDescriptor(foundDesc)
            }
        }

    }

    fun writeBytes(uuid: String, bytes: ByteArray) {
        println("[AppBluetoothGatt] writeBytes")
        btGatt?.services?.flatMap { it.characteristics }?.find { svcChar ->
            svcChar.uuid.toString() == uuid
        }?.also { foundChar ->
            println("Found Char: " + foundChar.uuid.toString())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                btGatt?.writeCharacteristic(
                    foundChar,
                    bytes,
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            } else {
                foundChar.setValue(bytes)
                btGatt?.writeCharacteristic(foundChar)
            }
        }

    }

    fun writeDescriptor(charUuid: String, uuid: String, bytes: ByteArray) {
        println("[AppBluetoothGatt] writeDescriptor")
        btGatt?.services?.flatMap { it.characteristics }?.flatMap { it.descriptors }
            ?.find { it.characteristic.uuid.toString() == charUuid && it.uuid.toString() == uuid }
            ?.also { foundDescriptor ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    btGatt?.writeDescriptor(foundDescriptor, bytes)
                } else {
                    foundDescriptor.setValue(bytes)
                    btGatt?.writeDescriptor(foundDescriptor)
                }
            }
    }

    fun close() {
        println("[AppBluetoothGatt] close")
        connectMessage.value = ConnectionState.DISCONNECTED
        try {
            btGatt?.let { gatt ->
                gatt.disconnect()
                gatt.close()
                btGatt = null
            }
        } catch (e: Exception) {
            println("BTGATT_CLOSE")
        } finally {
            btGatt = null
        }
    }

}