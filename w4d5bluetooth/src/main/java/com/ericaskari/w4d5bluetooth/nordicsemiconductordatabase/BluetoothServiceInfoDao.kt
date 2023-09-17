package com.ericaskari.w4d5bluetooth.nordicsemiconductordatabase

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
interface BluetoothServiceInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: BluetoothServiceInfo)

    @Delete
    suspend fun deleteItem(item: BluetoothServiceInfo)

    @Query("SELECT * from BluetoothServiceInfo WHERE identifier = :identifier")
    fun getItem(identifier: String): Flow<BluetoothServiceInfo>

    @Query("SELECT * from BluetoothServiceInfo ORDER BY name DESC")
    fun getAllItems(): Flow<List<BluetoothServiceInfo>>
}