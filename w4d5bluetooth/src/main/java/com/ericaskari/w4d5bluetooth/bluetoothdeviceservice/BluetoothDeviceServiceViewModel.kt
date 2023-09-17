package com.ericaskari.w4d5bluetooth.bluetoothdeviceservice

import androidx.lifecycle.ViewModel

class BluetoothDeviceServiceViewModel(
    private val repository: IBluetoothDeviceServiceRepository,
) : ViewModel() {
    val allItemsStream = repository.getAllItemsStream()
    fun getAllItemsByDeviceId(id: String) = repository.getAllItemsByDeviceId(id)

}