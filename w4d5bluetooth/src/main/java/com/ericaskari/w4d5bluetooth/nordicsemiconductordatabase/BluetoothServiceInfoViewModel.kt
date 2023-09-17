package com.ericaskari.w4d5bluetooth.nordicsemiconductordatabase

import androidx.lifecycle.ViewModel

class BluetoothServiceInfoViewModel(
    private val repository: IBluetoothServiceInfoRepository,
) : ViewModel() {
    val allItemsStream = repository.getAllItemsStream()

    fun syncBluetoothServiceInfo() {

    }

}