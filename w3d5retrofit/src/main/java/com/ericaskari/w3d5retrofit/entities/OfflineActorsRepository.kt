package com.ericaskari.w3d5retrofit.entities
import kotlinx.coroutines.flow.Flow

import com.example.exercise_5.ui.member.IActorRepository


/**
 * @author Mohammad Askari
 */
class OfflineActorsRepository(private val itemDao: ActorDao) : IActorRepository {
    override fun getAllItemsStream(): Flow<List<Actor>> = itemDao.getAllItems()

    override fun getItemStream(id: String): Flow<Actor?> = itemDao.getItem(id)

    override suspend fun insertItem(item: Actor) = itemDao.insert(item)

    override suspend fun insertItems(vararg items: Actor) = itemDao.insertItems(*items)

    override suspend fun deleteItem(item: Actor) = itemDao.delete(item)

    override suspend fun updateItem(item: Actor) = itemDao.update(item)
}