package com.ericaskari.w3d5retrofit.data

import android.content.Context
import com.ericaskari.w3d5retrofit.datasource.AppDatabase
import com.ericaskari.w3d5retrofit.entities.OfflineActorsRepository
import com.ericaskari.w3d5retrofit.entities.OfflineMoviesRepository
import com.example.exercise_5.ui.member.IActorRepository
import com.example.exercise_5.ui.member.IMovieRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val actorRepository: IActorRepository
    val movieRepository: IMovieRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
     override val actorRepository: IActorRepository by lazy {
         OfflineActorsRepository(AppDatabase.getInstance(context).actorDao())
    }
     override val movieRepository: IMovieRepository by lazy {
         OfflineMoviesRepository(AppDatabase.getInstance(context).movieDao())
    }
}