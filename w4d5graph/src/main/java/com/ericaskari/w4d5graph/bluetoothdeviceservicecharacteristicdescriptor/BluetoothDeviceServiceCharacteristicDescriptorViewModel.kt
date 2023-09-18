package com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristicdescriptor

import androidx.lifecycle.ViewModel

class BluetoothDeviceServiceCharacteristicDescriptorViewModel(
    private val repository: IBluetoothDeviceServiceCharacteristicDescriptorRepository,
) : ViewModel() {
    val allItemsStream = repository.getAllItemsStream()
    fun getAllItemsByCharacteristicId(id: String) = repository.getAllItemsByCharacteristicId(id)

}