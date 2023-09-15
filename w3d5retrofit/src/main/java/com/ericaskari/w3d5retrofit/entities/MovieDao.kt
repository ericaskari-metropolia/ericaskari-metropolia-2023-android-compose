package com.ericaskari.w3d5retrofit.entities

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * @author Mohammad Askari
 */
@Dao
interface MovieDao {
    @Query("SELECT * FROM Movie")
    fun find(): LiveData<List<Movie>>

    @Query("SELECT * FROM Movie WHERE id = :id")
    fun findById(id: String): LiveData<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg movies: Movie)

    @Delete
    fun delete(movie: Movie)

    @Query("DELETE FROM Movie")
    fun deleteAll()
}