package com.ericaskari.w4d5bluetooth.nordicsemiconductordatabase

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface IBluetoothServiceInfoRepository {
    fun getAllItemsStream(): Flow<List<BluetoothServiceInfo>>
    suspend fun insertItem(item: BluetoothServiceInfo)
    suspend fun syncItems(): Flow<List<BluetoothServiceInfo>>
    suspend fun deleteItem(item: BluetoothServiceInfo)

}