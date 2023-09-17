package com.ericaskari.w4d5bluetooth.bluetoothsearch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * @author Mohammad Askari
 */
@Dao
interface BluetoothDeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: BluetoothDevice)
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertItems(vararg items: Movie)
//
//    @Update
//    suspend fun update(item: Movie)
//
//    @Delete
//    suspend fun delete(item: Movie)

    @Query("SELECT * from BluetoothDevice WHERE address = :address")
    fun getItem(address: String): Flow<BluetoothDevice>

    @Query("SELECT * from BluetoothDevice ORDER BY deviceName DESC")
    fun getAllItems(): Flow<List<BluetoothDevice>>

    @Query(
        """delete from BluetoothDevice where 
        (julianday(CURRENT_TIMESTAMP)-julianday(datetime(lastSeen/1000, 'unixepoch'))) * 86400000 > 15000"""
    )
    suspend fun deleteNotSeen()

}