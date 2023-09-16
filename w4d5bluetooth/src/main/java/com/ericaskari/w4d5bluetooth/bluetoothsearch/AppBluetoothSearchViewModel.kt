package com.ericaskari.w4d5bluetooth.bluetoothsearch

import androidx.lifecycle.ViewModel

class AppBluetoothSearchViewModel(
    private val apBluetoothSearchRepository: IAppBluetoothSearchRepository,
) : ViewModel() {
    val allItemsStream = apBluetoothSearchRepository.getAllItemsStream()

}