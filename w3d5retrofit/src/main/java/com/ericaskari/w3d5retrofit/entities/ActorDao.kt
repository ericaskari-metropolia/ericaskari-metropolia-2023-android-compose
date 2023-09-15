package com.ericaskari.w3d5retrofit.entities

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * @author Mohammad Askari
 */
@Dao
interface ActorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Actor)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItems(vararg items: Actor)

    @Update
    suspend fun update(item: Actor)

    @Delete
    suspend fun delete(item: Actor)

    @Query("SELECT * from Actor WHERE id = :id")
    fun getItem(id: String): Flow<Actor>

    @Query("SELECT * from Actor ORDER BY name ASC")
    fun getAllItems(): Flow<List<Actor>>

}