package com.ericaskari.w4d5graph.bluetoothsearch

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface IBluetoothDeviceRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllItemsStream(): Flow<List<BluetoothDevice>>

    //
//    /**
//     * Retrieve an item from the given data source that matches with the [id].
//     */
    fun getItemStream(id: String): Flow<BluetoothDevice?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: BluetoothDevice)
    suspend fun deleteNotSeen()

//    /**
//     * Insert item in the data source
//     */
//    suspend fun insertItems(vararg items: Actor)
//
//    /**
//     * Delete item from the data source
//     */
//    suspend fun deleteItem(item: Actor)
//
//    /**
//     * Update item in the data source
//     */
//    suspend fun updateItem(item: Actor)
}