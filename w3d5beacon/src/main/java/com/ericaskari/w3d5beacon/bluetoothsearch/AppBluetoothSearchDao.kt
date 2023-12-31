package com.ericaskari.w3d5beacon.bluetoothsearch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * @author Mohammad Askari
 */
@Dao
interface AppBluetoothSearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
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

    @Query("SELECT * from AppBluetoothSearch WHERE address = :address")
    fun getItem(address: String): Flow<AppBluetoothSearch>

    @Query("SELECT * from AppBluetoothSearch ORDER BY address ASC")
    fun getAllItems(): Flow<List<AppBluetoothSearch>>

}