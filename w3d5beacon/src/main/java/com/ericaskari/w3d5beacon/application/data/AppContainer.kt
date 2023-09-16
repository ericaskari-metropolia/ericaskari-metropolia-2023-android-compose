package com.ericaskari.w3d5beacon.application.data

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.ericaskari.w3d5beacon.bluetoothsearch.AppBluetoothSearchRepository
import com.ericaskari.w3d5beacon.bluetoothsearch.IAppBluetoothSearchRepository
import com.ericaskari.w3d5beacon.datasource.AppDatabase


/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val appBluetoothSearchRepository: IAppBluetoothSearchRepository
//    val metropoliaRepository: IMetropoliaRepository
}

class AppDataContainer(private val context: Context, bluetoothManager: Lazy<BluetoothManager>, bluetoothAdapter: Lazy<BluetoothAdapter?>) :
    AppContainer {

    override val appBluetoothSearchRepository: IAppBluetoothSearchRepository by lazy {
        AppBluetoothSearchRepository(AppDatabase.getInstance(context).appBluetoothSearchDao())
    }

}