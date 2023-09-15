package com.ericaskari.w3d1room.metropolia

import com.ericaskari.w3d1room.api.apimodels.President


/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface IMetropoliaRepository {

    suspend fun getPresidents(): List<President>

}