package com.example.exercise_5.ui.member
import kotlinx.coroutines.flow.Flow

import com.ericaskari.w3d5retrofit.entities.Movie



/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface IMovieRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllItemsStream(): Flow<List<Movie>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: String): Flow<Movie?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: Movie)

    /**
     * Insert item in the data source
     */
    suspend fun insertItems(vararg items: Movie)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: Movie)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: Movie)
}