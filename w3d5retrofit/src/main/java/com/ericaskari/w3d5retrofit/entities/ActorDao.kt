package com.ericaskari.w3d5retrofit.entities

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * @author Mohammad Askari
 */
@Dao
interface ActorDao {
    @Query("SELECT * FROM Actor")
    fun find(): LiveData<List<Actor>>

    @Query("SELECT * FROM Actor WHERE movieId = :id")
    fun findById(id: String): LiveData<List<Actor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg actors: Actor)

    @Delete
    fun delete(actor: Actor)

    @Query("DELETE FROM Actor")
    fun deleteAll()
}