package com.ericaskari.w4d5graph.application

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import androidx.activity.ComponentActivity
import com.ericaskari.w4d5graph.application.connectivity.AppConnectivityManager
import com.ericaskari.w4d5graph.application.connectivity.InternetConnectionState
import com.ericaskari.w4d5graph.application.data.AppContainer
import com.ericaskari.w4d5graph.application.data.AppDataContainer
import com.ericaskari.w4d5graph.bluetooth.AppBluetoothGattService
import com.ericaskari.w4d5graph.bluetooth.AppBluetoothManager
import com.ericaskari.w4d5graph.bluetooth.AppBluetoothObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MyApplication : Application() {
    val TAG = "[MyApplication]"

    companion object {
        private val parentJob = Job()
        private val coroutineScope = CoroutineScope(Dispatchers.Default + parentJob)
    }

    val internetConnectionState = MutableStateFlow(InternetConnectionState.UNKNOWN)


    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */

    lateinit var appConnectivityManager: AppConnectivityManager

    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var appBluetoothManager: AppBluetoothManager
    lateinit var container: AppContainer
    lateinit var appBluetoothObserver: AppBluetoothObserver;
    lateinit var appBluetoothGattService: AppBluetoothGattService;

    override fun onCreate() {
        super.onCreate()
        appConnectivityManager = AppConnectivityManager(this) {
            println("$TAG[AppConnectivityManager] ${it.toTitle()}")
            internetConnectionState.value = it
        }
        container = AppDataContainer(this)
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        appBluetoothManager = AppBluetoothManager(
            bluetoothAdapter = bluetoothAdapter,
            scope = coroutineScope,
            bluetoothDeviceRepository = container.bluetoothDeviceRepository
        )
        appBluetoothGattService = AppBluetoothGattService(
            btAdapter = bluetoothAdapter,
            scope = coroutineScope,
            app = this,
            bluetoothServiceInfoRepository = container.bluetoothServiceInfoRepository,
            bluetoothDeviceServiceRepository = container.bluetoothDeviceServiceRepository,
            bluetoothDeviceServiceCharacteristicRepository = container.bluetoothDeviceServiceCharacteristicRepository,
            bluetoothDeviceServiceCharacteristicDescriptorRepository = container.bluetoothDeviceServiceCharacteristicDescriptorRepository
        )

        coroutineScope.launch {
            val state = internetConnectionState.first()
            if (state.isConnected()) {
                container.bluetoothServiceInfoRepository.syncItems()
            } else {
                println("Skipping nordicsemiconductordatabase download since there is no internet")
            }
        }

    }

    fun initializeAppBluetoothObserver(activity: ComponentActivity): AppBluetoothObserver {
        println("[MyApplication] initializeAppBluetoothObserver Called")
        appBluetoothObserver = AppBluetoothObserver(
            activity = activity,
            bluetoothAdapter = bluetoothAdapter,
            appBluetoothManager = appBluetoothManager,
        )

        return appBluetoothObserver
    }
}