package com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristic

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
interface BluetoothDeviceServiceCharacteristicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: BluetoothDeviceServiceCharacteristic)

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertItems(vararg items: BluetoothDeviceServiceCharacteristic)
//
//    @Update
//    suspend fun update(item: Movie)
    @Delete
    suspend fun deleteItem(item: BluetoothDeviceServiceCharacteristic)

    @Query("SELECT * from BluetoothDeviceServiceCharacteristic WHERE id = :id AND serviceId = :serviceId")
    fun getItem(id: String, serviceId: String): Flow<BluetoothDeviceServiceCharacteristic>

    @Query("SELECT * from BluetoothDeviceServiceCharacteristic")
    fun getAllItems(): Flow<List<BluetoothDeviceServiceCharacteristic>>

    @Query("SELECT * from BluetoothDeviceServiceCharacteristic WHERE serviceId = :id ORDER BY id DESC")
    fun getAllItemsByServiceId(id: String): Flow<List<BluetoothDeviceServiceCharacteristic>>
}