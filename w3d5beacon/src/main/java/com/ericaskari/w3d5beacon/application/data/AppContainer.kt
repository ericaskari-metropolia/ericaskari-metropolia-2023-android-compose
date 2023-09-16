package com.ericaskari.w3d5beacon.application.data

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context


/**
 * App container for Dependency injection.
 */
interface AppContainer {
//    val wikiRepository: IWikiRepository
//    val metropoliaRepository: IMetropoliaRepository
}

class AppDataContainer(private val context: Context, bluetoothManager: Lazy<BluetoothManager>, bluetoothAdapter: Lazy<BluetoothAdapter?>) :
    AppContainer {
//    override val wikiRepository: IWikiRepository by lazy {
//        WikiRepository(RetrofitFactory.makeWikiApiService())
//    }
//    override val metropoliaRepository: IMetropoliaRepository by lazy {
//        MetropoliaRepository(RetrofitFactory.makeMetropoliaApiService())
//    }
}