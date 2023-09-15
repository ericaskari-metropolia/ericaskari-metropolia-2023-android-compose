package com.ericaskari.w3d5retrofit.entities
import kotlinx.coroutines.flow.Flow

import com.example.exercise_5.ui.member.IMovieRepository


/**
 * @author Mohammad Askari
 */
class OfflineMoviesRepository(private val itemDao: MovieDao) : IMovieRepository {
    override fun getAllItemsStream(): Flow<List<Movie>> = itemDao.getAllItems()

    override fun getItemStream(id: String): Flow<Movie?> = itemDao.getItem(id)

    override suspend fun insertItem(item: Movie) = itemDao.insert(item)

    override suspend fun insertItems(vararg items: Movie) = itemDao.insertItems(*items)

    override suspend fun deleteItem(item: Movie) = itemDao.delete(item)

    override suspend fun updateItem(item: Movie) = itemDao.update(item)
}