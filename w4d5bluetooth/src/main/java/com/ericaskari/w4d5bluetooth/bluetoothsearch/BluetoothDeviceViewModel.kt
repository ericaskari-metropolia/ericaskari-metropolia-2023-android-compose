package com.ericaskari.w4d5bluetooth.bluetoothsearch

import androidx.lifecycle.ViewModel

class BluetoothDeviceViewModel(
    private val repository: IBluetoothDeviceRepository,
) : ViewModel() {
    val allItemsStream = repository.getAllItemsStream()

}