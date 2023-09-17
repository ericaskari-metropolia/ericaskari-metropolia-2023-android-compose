package com.ericaskari.w4d5bluetooth.bluetoothconnect

import androidx.lifecycle.ViewModel
import com.ericaskari.w4d5bluetooth.bluetooth.AppBluetoothGattService
import kotlinx.coroutines.delay

class AppBluetoothConnectViewModel(
    private val appBluetoothGattService: AppBluetoothGattService,
) : ViewModel() {
    fun connect(address: String) {
        appBluetoothGattService.connect(address)
    }

    suspend fun readAll() {
        appBluetoothGattService.readAll()

    }

    suspend fun readCharacteristic() {
        appBluetoothGattService.readCharacteristic("00002a00-0000-1000-8000-00805f9b34fb")
        delay(1000)
        appBluetoothGattService.readCharacteristic("00002a01-0000-1000-8000-00805f9b34fb")
        delay(1000)
        appBluetoothGattService.readCharacteristic("00002a05-0000-1000-8000-00805f9b34fb")
        delay(1000)
        appBluetoothGattService.readCharacteristic("00002a29-0000-1000-8000-00805f9b34fb")
        delay(1000)
        appBluetoothGattService.readCharacteristic("00002a24-0000-1000-8000-00805f9b34fb")
    }

    suspend fun readDescriptor() {
        appBluetoothGattService.readDescriptor("00002a01-0000-1000-8000-00805f9b34fb", "00002800-0000-1000-8000-00805f9b34fb")
        delay(1000)
        appBluetoothGattService.readDescriptor("00002a05-0000-1000-8000-00805f9b34fb", "00002902-0000-1000-8000-00805f9b34fb")
        delay(1000)
        appBluetoothGattService.readDescriptor("00002a05-0000-1000-8000-00805f9b34fb", "00002800-0000-1000-8000-00805f9b34fb")
        delay(1000)
    }
}