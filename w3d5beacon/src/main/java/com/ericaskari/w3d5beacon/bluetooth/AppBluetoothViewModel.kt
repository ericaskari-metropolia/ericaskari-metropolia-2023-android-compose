package com.ericaskari.w3d5beacon.bluetooth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AppBluetoothViewModel(
    private val appBluetoothManager: AppBluetoothManager,

    ) : ViewModel() {
    val isScanning by mutableStateOf(appBluetoothManager.isScanning)
    val scannerMessage = appBluetoothManager.userMessage


    fun startScan() {
        appBluetoothManager.scanEnabled = true
        appBluetoothManager.scan()
    }

    fun stopScan() {
        appBluetoothManager.scanEnabled = false
        appBluetoothManager.stopScan()
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