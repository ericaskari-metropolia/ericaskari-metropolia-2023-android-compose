package com.ericaskari.w4d5graph.bluetoothdeviceservicevalue

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow

class BluetoothDeviceServiceValueViewModel(
    private val repository: IBluetoothDeviceServiceValueRepository,
) : ViewModel() {
    fun getAllItemsByServiceIdStream(deviceId: String, serviceId: String): Flow<List<BluetoothDeviceServiceValue>> {
        return repository.getAllItemsByServiceIdStream(deviceId, serviceId)
    }

}