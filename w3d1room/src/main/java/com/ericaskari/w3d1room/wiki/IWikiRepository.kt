package com.ericaskari.w3d1room.wiki


/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface IWikiRepository {

    suspend fun getHits(search: String): Int

}