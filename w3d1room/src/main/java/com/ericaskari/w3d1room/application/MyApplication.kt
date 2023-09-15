package com.ericaskari.w3d1room.application

import android.app.Application
import com.ericaskari.w3d1room.application.data.AppContainer
import com.ericaskari.w3d1room.application.data.AppDataContainer

class MyApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}