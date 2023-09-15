package com.ericaskari.w3d5retrofit.application

import android.app.Application
import com.ericaskari.w3d5retrofit.datasource.AppDatabase
import com.ericaskari.w3d5retrofit.entities.OfflineActorsRepository
import com.example.exercise_5.ui.member.MovieRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


/**
 * @author Mohammad Askari
 */
class W3D5RetrofitApplication : Application() {
    //  App Scope
    private val applicationScope = CoroutineScope(SupervisorJob())

    //  App Database
    private val appDatabase by lazy { AppDatabase.getInstance(this, applicationScope) }

    //  App Repositories
    val actorRepository by lazy { OfflineActorsRepository(appDatabase.actorDao()) }
    val movieRepository by lazy { MovieRepository(appDatabase.movieDao()) }
}