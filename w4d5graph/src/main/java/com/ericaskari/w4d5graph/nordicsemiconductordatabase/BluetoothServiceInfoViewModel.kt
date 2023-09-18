package com.ericaskari.w4d5graph.nordicsemiconductordatabase

import androidx.lifecycle.ViewModel

class BluetoothServiceInfoViewModel(
    private val repository: IBluetoothServiceInfoRepository,
) : ViewModel() {
    val allItemsStream = repository.getAllItemsStream()

    fun syncBluetoothServiceInfo() {

    }

}