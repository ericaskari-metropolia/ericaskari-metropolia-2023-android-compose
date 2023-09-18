package com.ericaskari.w4d5graph.bluetoothdeviceservicevalue

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
interface BluetoothDeviceServiceValueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: BluetoothDeviceServiceValue)

    @Delete
    fun deleteItem(item: BluetoothDeviceServiceValue)


    @Query("SELECT * from BluetoothDeviceServiceValue WHERE id = :id")
    fun getItemStream(id: Int): Flow<BluetoothDeviceServiceValue>

    @Query("SELECT * from BluetoothDeviceServiceValue ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    fun getAllItemsStream(limit: Int = 10, offset: Int = 0): Flow<List<BluetoothDeviceServiceValue>>

    @Query("SELECT * from BluetoothDeviceServiceValue WHERE deviceId = :id ORDER BY id DESC")
    fun getAllItemsByDeviceIdStream(id: String): Flow<List<BluetoothDeviceServiceValue>>

    @Query("SELECT * from BluetoothDeviceServiceValue WHERE deviceId = :deviceId AND serviceId = :serviceId ORDER BY id DESC")
    fun getAllItemsByServiceIdStream(deviceId: String, serviceId: String): Flow<List<BluetoothDeviceServiceValue>>


}