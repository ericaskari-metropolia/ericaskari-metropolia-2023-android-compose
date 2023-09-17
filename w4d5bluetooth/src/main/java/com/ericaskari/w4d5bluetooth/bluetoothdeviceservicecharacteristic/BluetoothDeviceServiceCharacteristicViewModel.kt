package com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic

import androidx.lifecycle.ViewModel

class BluetoothDeviceServiceCharacteristicViewModel(
    private val repository: IBluetoothDeviceServiceCharacteristicRepository,
) : ViewModel() {
    val allItemsStream = repository.getAllItemsStream()
    fun getAllItemsByServiceId(id: String) = repository.getAllItemsByServiceId(id)
    fun getItemStream(id: String, serviceId: String) = repository.getItemStream(id, serviceId)

    fun setCharacteristicNotification(item: BluetoothDeviceServiceCharacteristic) {

    }
}