package com.ericaskari.w4d5bluetooth.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import com.ericaskari.w4d5bluetooth.bluetoothsearch.BluetoothDevice
import com.ericaskari.w4d5bluetooth.bluetoothsearch.IBluetoothDeviceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AppBluetoothManager(
    private val bluetoothAdapter: BluetoothAdapter,
    private val scope: CoroutineScope,
    private val bluetoothDeviceRepository: IBluetoothDeviceRepository,
) {
    val isScanning = MutableStateFlow(false)

    var scanEnabled = false

    private var lastCleanupTimestamp: Long? = null
    private val CLEANUP_DURATION = 60000L

    //  Configuration for bluetooth scanning


    private val scanCallback = object : ScanCallback() {

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)

            println("[AppBluetoothManager] onScanFailed")
            println("Scan error $errorCode")

            if (errorCode == SCAN_FAILED_ALREADY_STARTED) {
                stopScan()
                scan()
            }
        }

        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)

            scope.launch {
                bluetoothDeviceRepository.insertItem(BluetoothDevice.fromScanResult(result))

                if (isScanning.value) {
                    launch { deleteNotSeen() }
                }
            }

        }

    }

    suspend fun deleteNotSeen() {
        val prefix = "[AppBluetoothManager][deleteNotSeen]"
        lastCleanupTimestamp?.let {
            if (System.currentTimeMillis() - it > CLEANUP_DURATION) {
                bluetoothDeviceRepository.deleteNotSeen()
                println("$prefix deleted not seen")
                lastCleanupTimestamp = System.currentTimeMillis()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun scan() {
        val prefix = "[AppBluetoothManager][scan]"
        println(prefix)

        if (!scanEnabled) {
            return
        }
        if (!bluetoothAdapter.isEnabled) {
            return
        }

        isScanning.value = true

        try {
            bluetoothAdapter.bluetoothLeScanner.startScan(null, scanSettings, scanCallback)
            lastCleanupTimestamp = System.currentTimeMillis()

        } catch (e: Exception) {
            println("scan ERROR")
            println(e)
            println(e.message)
            isScanning.value = false
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        isScanning.value = false

        println("[AppBluetoothManager] stopScan")
        try {
            bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)

        } catch (e: Exception) {
            println(e.message)
        }
    }


    companion object {
        private val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
    }
}