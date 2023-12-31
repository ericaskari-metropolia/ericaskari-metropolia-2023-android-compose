package com.ericaskari.w4d5bluetooth.application

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import androidx.activity.ComponentActivity
import com.ericaskari.w4d5bluetooth.application.data.AppContainer
import com.ericaskari.w4d5bluetooth.application.data.AppDataContainer
import com.ericaskari.w4d5bluetooth.bluetooth.AppBluetoothGattService
import com.ericaskari.w4d5bluetooth.bluetooth.AppBluetoothManager
import com.ericaskari.w4d5bluetooth.bluetooth.AppBluetoothObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyApplication : Application() {
    companion object {
        private val parentJob = Job()
        private val coroutineScope = CoroutineScope(Dispatchers.Default + parentJob)
    }

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */

    lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var appBluetoothManager: AppBluetoothManager
    lateinit var container: AppContainer
    lateinit var appBluetoothObserver: AppBluetoothObserver;
    lateinit var appBluetoothGattService: AppBluetoothGattService;

    override fun onCreate() {
        super.onCreate()
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
            container.bluetoothServiceInfoRepository.syncItems()
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