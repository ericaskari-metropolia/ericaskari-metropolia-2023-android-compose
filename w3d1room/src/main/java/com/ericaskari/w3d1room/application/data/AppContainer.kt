package com.ericaskari.w3d1room.application.data

import android.content.Context
import com.ericaskari.w3d1room.api.RetrofitFactory
import com.ericaskari.w3d1room.metropolia.IMetropoliaRepository
import com.ericaskari.w3d1room.metropolia.MetropoliaRepository
import com.ericaskari.w3d1room.wiki.IWikiRepository
import com.ericaskari.w3d1room.wiki.WikiRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val wikiRepository: IWikiRepository
    val metropoliaRepository: IMetropoliaRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val wikiRepository: IWikiRepository by lazy {
        WikiRepository(RetrofitFactory.makeWikiApiService())
    }
    override val metropoliaRepository: IMetropoliaRepository by lazy {
        MetropoliaRepository(RetrofitFactory.makeMetropoliaApiService())
    }
}