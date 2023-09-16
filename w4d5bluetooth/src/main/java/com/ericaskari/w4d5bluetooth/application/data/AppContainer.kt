package com.ericaskari.w4d5bluetooth.application.data

import android.content.Context
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservice.BluetoothDeviceServiceRepository
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservice.IBluetoothDeviceServiceRepository
import com.ericaskari.w4d5bluetooth.bluetoothsearch.AppBluetoothSearchRepository
import com.ericaskari.w4d5bluetooth.bluetoothsearch.IAppBluetoothSearchRepository
import com.ericaskari.w4d5bluetooth.datasource.AppDatabase


/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val appBluetoothSearchRepository: IAppBluetoothSearchRepository
    val bluetoothDeviceServiceRepository: IBluetoothDeviceServiceRepository
}

class AppDataContainer(private val context: Context) :
    AppContainer {

    override val appBluetoothSearchRepository: IAppBluetoothSearchRepository by lazy {
        AppBluetoothSearchRepository(AppDatabase.getInstance(context).appBluetoothSearchDao())
    }

    override val bluetoothDeviceServiceRepository: IBluetoothDeviceServiceRepository by lazy {
        BluetoothDeviceServiceRepository(AppDatabase.getInstance(context).bluetoothDeviceServiceDao())
    }

}