package com.ericaskari.w3d5beacon.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AppBluetoothManager(
    private val bluetoothAdapter: BluetoothAdapter,
//    private val bleRepository: IBleRepository,
    private val scope: CoroutineScope,
//    private val parseScanResult: ParseScanResult
) {

    private val btScanner = bluetoothAdapter.bluetoothLeScanner

    val userMessage = MutableStateFlow<String?>(null)
    val isScanning = MutableStateFlow(false)

    var scanEnabled = false

    private var lastCleanupTimestamp: Long? = null
    private val CLEANUP_DURATION = 60000L

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private val scanCallback = object : ScanCallback() {

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            println("[AppBluetoothManager] onScanFailed")
            println("Scan error $errorCode")
            if (errorCode == 1) {
                stopScan()
                scan()
            }
        }

        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            println("[AppBluetoothManager] onScanResult")
            super.onScanResult(callbackType, result)

            println("scanEnabled: $scanEnabled")

            scope.launch {
//                parseScanResult(result)
                if (isScanning.value)
                    launch { deleteNotSeen() }
            }

        }

    }

    suspend fun deleteNotSeen() {
        println("[AppBluetoothManager] deleteNotSeen")
        lastCleanupTimestamp?.let {
            if (System.currentTimeMillis() - it > CLEANUP_DURATION) {
//                bleRepository.deleteNotSeen()
                println("deleted not seen")
                lastCleanupTimestamp = System.currentTimeMillis()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun scan() {
        println("[AppBluetoothManager] scan")
        println("[AppBluetoothManager] scanEnabled:                 $scanEnabled")
        println("[AppBluetoothManager] bluetoothAdapter.isEnabled:  ${bluetoothAdapter.isEnabled}")
        try {
            if (scanEnabled) {
                if (bluetoothAdapter.isEnabled) {
                    isScanning.value = true
                    btScanner?.startScan(null, scanSettings, scanCallback) ?: println("btScanner is null")
                    lastCleanupTimestamp = System.currentTimeMillis()
                    println("started scan")
                } else {
                    userMessage.value = "You must enable Bluetooth to start scanning."
                }
            }
        } catch (e: Exception) {
            println("START_SCAN")
            println(e)
            println(e.message)
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        println("[AppBluetoothManager] stopScan")
        try {
            if (bluetoothAdapter.isEnabled)
                btScanner.stopScan(scanCallback)
        } catch (e: Exception) {
            println(e.message)
        } finally {
            isScanning.value = false
        }
    }

    fun userMessageShown() {
        userMessage.value = null
    }

}