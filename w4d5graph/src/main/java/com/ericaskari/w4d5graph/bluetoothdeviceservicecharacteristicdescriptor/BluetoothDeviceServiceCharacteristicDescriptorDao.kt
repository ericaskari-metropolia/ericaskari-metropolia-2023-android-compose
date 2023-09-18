package com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristicdescriptor

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
interface BluetoothDeviceServiceCharacteristicDescriptorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: BluetoothDeviceServiceCharacteristicDescriptor)

    //    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertItems(vararg items: BluetoothDeviceServiceCharacteristic)
//
//    @Update
//    suspend fun update(item: Movie)
//
    @Delete
    suspend fun deleteItem(item: BluetoothDeviceServiceCharacteristicDescriptor)

    @Query("SELECT * from BluetoothDeviceServiceCharacteristicDescriptor WHERE id = :id")
    fun getItem(id: String): Flow<BluetoothDeviceServiceCharacteristicDescriptor>

    @Query("SELECT * from BluetoothDeviceServiceCharacteristicDescriptor")
    fun getAllItems(): Flow<List<BluetoothDeviceServiceCharacteristicDescriptor>>

    @Query("SELECT * from BluetoothDeviceServiceCharacteristicDescriptor WHERE characteristicId = :id ORDER BY id DESC")
    fun getAllItemsByCharacteristicId(id: String): Flow<List<BluetoothDeviceServiceCharacteristicDescriptor>>

}