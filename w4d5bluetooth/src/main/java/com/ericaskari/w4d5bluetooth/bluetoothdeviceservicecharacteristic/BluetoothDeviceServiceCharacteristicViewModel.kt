package com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic

import androidx.lifecycle.ViewModel

class BluetoothDeviceServiceCharacteristicViewModel(
    private val repository: IBluetoothDeviceServiceCharacteristicRepository,
) : ViewModel() {
    val allItemsStream = repository.getAllItemsStream()

}