package com.ericaskari.w4d5graph.bluetoothdeviceservice

import androidx.lifecycle.ViewModel

class BluetoothDeviceServiceViewModel(
    private val repository: IBluetoothDeviceServiceRepository,
) : ViewModel() {
    val allItemsStream = repository.getAllItemsStream()
    fun getItemStream(id: String) = repository.getItemStream(id)
    fun getAllItemsByDeviceId(id: String) = repository.getAllItemsByDeviceId(id)

}