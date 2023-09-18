package com.ericaskari.w4d5graph.bluetoothsearch

import androidx.lifecycle.ViewModel

class BluetoothDeviceViewModel(
    private val repository: IBluetoothDeviceRepository,
) : ViewModel() {
    val allItemsStream = repository.getAllItemsStream()
    fun getItemStream(id: String) = repository.getItemStream(id)

}