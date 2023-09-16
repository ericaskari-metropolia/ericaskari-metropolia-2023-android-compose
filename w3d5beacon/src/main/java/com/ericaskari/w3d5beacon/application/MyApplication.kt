package com.ericaskari.w3d5beacon.application

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import com.ericaskari.w3d5beacon.application.data.AppContainer
import com.ericaskari.w3d5beacon.application.data.AppDataContainer
import com.ericaskari.w3d5beacon.bluetooth.AppBluetoothManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MyApplication : Application() {
    companion object {
        private val parentJob = Job()
        private val coroutineScope = CoroutineScope(Dispatchers.Default + parentJob)
    }

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    val bluetoothManager: Lazy<BluetoothManager> = lazy { getSystemService(BluetoothManager::class.java) }
    val bluetoothAdapter: Lazy<BluetoothAdapter?> = lazy { bluetoothManager.value.adapter }
    val appBluetoothManager: Lazy<AppBluetoothManager?> = lazy {
        bluetoothAdapter.value?.let {
            AppBluetoothManager(
                bluetoothAdapter = it,
                scope = coroutineScope,
                appBluetoothSearchRepository = container.appBluetoothSearchRepository
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this, bluetoothManager, bluetoothAdapter)
    }
}