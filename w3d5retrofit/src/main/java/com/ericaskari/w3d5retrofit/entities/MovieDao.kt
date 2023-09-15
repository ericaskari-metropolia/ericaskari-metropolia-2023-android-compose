package com.ericaskari.w3d5retrofit.entities

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * @author Mohammad Askari
 */
@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Movie)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItems(vararg items: Movie)

    @Update
    suspend fun update(item: Movie)

    @Delete
    suspend fun delete(item: Movie)

    @Query("SELECT * from Movie WHERE id = :id")
    fun getItem(id: String): Flow<Movie>

    @Query("SELECT * from Movie ORDER BY name ASC")
    fun getAllItems(): Flow<List<Movie>>

}