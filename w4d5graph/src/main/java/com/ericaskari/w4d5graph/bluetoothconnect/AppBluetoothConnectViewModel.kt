package com.ericaskari.w4d5graph.bluetoothconnect

import androidx.lifecycle.ViewModel
import com.ericaskari.w4d5graph.bluetooth.AppBluetoothGattService
import kotlinx.coroutines.flow.Flow

class AppBluetoothConnectViewModel(
    private val appBluetoothGattService: AppBluetoothGattService,
) : ViewModel() {
    fun readCharacteristic(uuid: String) = appBluetoothGattService.readCharacteristic(uuid)
    val connectMessage = appBluetoothGattService.connectMessage
    val output: Flow<ByteArray?> = appBluetoothGattService.output

    fun close() = appBluetoothGattService.close()
    fun connect(address: String) {
        appBluetoothGattService.connect(address)
    }

    suspend fun writeDescriptor(
        serviceId: String,
        characteristicId: String,
        descriptorId: String,
        value: ByteArray
    ) {
        appBluetoothGattService.writeDescriptor(
            serviceId = serviceId,
            characteristicId = characteristicId,
            descriptorId = descriptorId,
            value = value
        )
    }

    suspend fun writeCharacteristic(
        serviceId: String,
        characteristicId: String,
        value: ByteArray
    ) {
        appBluetoothGattService.writeCharacteristic(
            serviceId = serviceId,
            characteristicId = characteristicId,
            value = value
        )
    }

    suspend fun enableNotificationsAndIndications(
        deviceId: String,
        serviceId: String,
        characteristicId: String,
    ) {
        return appBluetoothGattService.enableNotificationsAndIndications(
            deviceId = deviceId,
            serviceId = serviceId,
            characteristicId = characteristicId,
        )
    }
}