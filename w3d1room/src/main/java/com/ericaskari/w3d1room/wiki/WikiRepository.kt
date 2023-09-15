package com.ericaskari.w3d1room.wiki

import com.ericaskari.w3d1room.api.WikiApi
import retrofit2.await


/**
 * @author Mohammad Askari
 */
class WikiRepository(private val apiService: WikiApi) : IWikiRepository {
    override suspend fun getHits(search: String): Int {
        val result = apiService.getHits(search).await()

        return result.query.searchinfo.totalhits
    }
}