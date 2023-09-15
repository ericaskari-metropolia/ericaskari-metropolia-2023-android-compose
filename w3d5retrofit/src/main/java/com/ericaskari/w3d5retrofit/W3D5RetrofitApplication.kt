package com.ericaskari.w3d5retrofit

import android.app.Application
import com.ericaskari.w3d5retrofit.data.AppContainer
import com.ericaskari.w3d5retrofit.data.AppDataContainer
import com.ericaskari.w3d5retrofit.datasource.AppDatabase
import com.ericaskari.w3d5retrofit.entities.OfflineActorsRepository
import com.ericaskari.w3d5retrofit.entities.OfflineMoviesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


/**
 * @author Mohammad Askari
 */

class W3D5RetrofitApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }}