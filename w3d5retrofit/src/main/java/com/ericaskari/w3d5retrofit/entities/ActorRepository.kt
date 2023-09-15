package com.example.exercise_5.ui.member
import kotlinx.coroutines.flow.Flow

import com.ericaskari.w3d5retrofit.entities.Actor
import com.ericaskari.w3d5retrofit.entities.ActorDao



/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface IActorRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllItemsStream(): Flow<List<Actor>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: String): Flow<Actor?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: Actor)

    /**
     * Insert item in the data source
     */
    suspend fun insertItems(vararg items: Actor)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: Actor)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: Actor)
}