package com.ericaskari.w4d5bluetooth.bluetooth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AppBluetoothViewModel(
    private val appBluetoothManager: AppBluetoothManager,
    private val appBluetoothObserver: AppBluetoothObserver,
) : ViewModel() {
    val isScanning by mutableStateOf(appBluetoothManager.isScanning)
    val bluetoothAdapterState = appBluetoothObserver.bluetoothAdapterState


    fun startScan() {
        appBluetoothManager.scanEnabled = true
        appBluetoothManager.scan()
    }

    fun stopScan() {
        appBluetoothManager.scanEnabled = false
        appBluetoothManager.stopScan()
    }

    fun askToTurnBluetoothOn() {
        appBluetoothObserver.launchEnableBtAdapter()
    }

//    fun getReadBytes(): ByteArray? {
//        return _services.value.let { svcs ->
//            svcs.flatMap { it.characteristics }.find {
//                it.uuid == ELKBLEDOM.uuid
//            }?.readBytes
//        }
//    }
//    fun getOnOffState(): Boolean {
//        val bytes = getReadBytes()
//        return bytes?.let {
//            ELKBLEDOM.onOffState(it)
//        } ?: false
//    }
}