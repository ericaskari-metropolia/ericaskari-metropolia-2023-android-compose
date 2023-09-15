package com.ericaskari.w3d1room.metropolia

import com.ericaskari.w3d1room.api.MetropoliaApi
import com.ericaskari.w3d1room.api.apimodels.President
import retrofit2.await


/**
 * @author Mohammad Askari
 */
class MetropoliaRepository(private val apiService: MetropoliaApi) : IMetropoliaRepository {
    override suspend fun getPresidents(): List<President> {
        return apiService.getPresidents().await()
    }
}