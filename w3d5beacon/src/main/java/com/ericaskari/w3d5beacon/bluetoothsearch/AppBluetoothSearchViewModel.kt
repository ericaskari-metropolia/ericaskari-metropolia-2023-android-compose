package com.ericaskari.w3d5beacon.bluetoothsearch

import androidx.lifecycle.ViewModel

class AppBluetoothSearchViewModel(
    private val apBluetoothSearchRepository: IAppBluetoothSearchRepository,
) : ViewModel() {
    val allItemsStream = apBluetoothSearchRepository.getAllItemsStream()

}