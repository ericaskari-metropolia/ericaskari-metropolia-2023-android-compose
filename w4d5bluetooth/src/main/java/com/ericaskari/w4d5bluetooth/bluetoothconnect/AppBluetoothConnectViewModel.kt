package com.ericaskari.w4d5bluetooth.bluetoothconnect

import androidx.lifecycle.ViewModel
import com.ericaskari.w4d5bluetooth.bluetooth.AppBluetoothGattService

class AppBluetoothConnectViewModel(
    private val appBluetoothGattService: AppBluetoothGattService,
) : ViewModel() {
    fun connect(address: String) {
        appBluetoothGattService.connect(address)
    }
}