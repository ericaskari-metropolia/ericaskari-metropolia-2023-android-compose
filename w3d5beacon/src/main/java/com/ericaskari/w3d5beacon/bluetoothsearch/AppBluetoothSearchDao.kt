package com.ericaskari.w3d5beacon.bluetoothsearch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

/**
 * @author Mohammad Askari
 */
@Dao
interface AppBluetoothSearchDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: AppBluetoothSearch)
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertItems(vararg items: Movie)
//
//    @Update
//    suspend fun update(item: Movie)
//
//    @Delete
//    suspend fun delete(item: Movie)
//
//    @Query("SELECT * from Movie WHERE id = :id")
//    fun getItem(id: String): Flow<Movie>
//
//    @Query("SELECT * from Movie ORDER BY name ASC")
//    fun getAllItems(): Flow<List<Movie>>

}