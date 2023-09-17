package com.ericaskari.w4d5bluetooth.bluetoothdeviceservice

import androidx.lifecycle.ViewModel

class BluetoothDeviceServiceViewModel(
    private val repository: IBluetoothDeviceServiceRepository,
) : ViewModel() {
    val allItemsStream = repository.getAllItemsStream()
    fun getAllItemsByDeviceAddressStream(deviceAddress: String) = repository.getAllItemsByDeviceAddressStream(deviceAddress)

}