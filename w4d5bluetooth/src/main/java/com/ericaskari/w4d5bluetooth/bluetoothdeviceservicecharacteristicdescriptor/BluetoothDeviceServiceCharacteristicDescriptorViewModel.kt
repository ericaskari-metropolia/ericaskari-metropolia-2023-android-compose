package com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor

import androidx.lifecycle.ViewModel

class BluetoothDeviceServiceCharacteristicDescriptorViewModel(
    private val repository: IBluetoothDeviceServiceCharacteristicDescriptorRepository,
) : ViewModel() {
    val allItemsStream = repository.getAllItemsStream()

}