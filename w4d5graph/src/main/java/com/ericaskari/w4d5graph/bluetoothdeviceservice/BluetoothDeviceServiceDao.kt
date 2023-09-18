package com.ericaskari.w4d5graph.bluetoothdeviceservice

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * @author Mohammad Askari
 */
@Dao
interface BluetoothDeviceServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: BluetoothDeviceService)

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertItems(vararg items: BluetoothDeviceService)
//
//    @Update
//    suspend fun update(item: Movie)
//
    @Delete
    suspend fun deleteItem(item: BluetoothDeviceService)

    @Query("SELECT * from BluetoothDeviceService WHERE id = :id")
    fun getItem(id: String): Flow<BluetoothDeviceService>

    @Query("SELECT * from BluetoothDeviceService ORDER BY deviceAddress DESC")
    fun getAllItems(): Flow<List<BluetoothDeviceService>>

    @Query("SELECT * from BluetoothDeviceService WHERE deviceAddress = :id ORDER BY id DESC")
    fun getAllItemsByDeviceId(id: String): Flow<List<BluetoothDeviceService>>


}