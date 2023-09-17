package com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * @author Mohammad Askari
 */
@Dao
interface BluetoothDeviceServiceCharacteristicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: BluetoothDeviceServiceCharacteristic)

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertItems(vararg items: BluetoothDeviceServiceCharacteristic)
//
//    @Update
//    suspend fun update(item: Movie)
//
//    @Delete
//    suspend fun delete(item: Movie)

    @Query("SELECT * from BluetoothDeviceServiceCharacteristic WHERE id = :id")
    fun getItem(id: String): Flow<BluetoothDeviceServiceCharacteristic>

    @Query("SELECT * from BluetoothDeviceServiceCharacteristic")
    fun getAllItems(): Flow<List<BluetoothDeviceServiceCharacteristic>>

}