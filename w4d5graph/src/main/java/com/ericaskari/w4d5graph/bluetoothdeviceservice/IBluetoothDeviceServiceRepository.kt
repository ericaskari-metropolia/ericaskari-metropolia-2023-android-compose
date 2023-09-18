package com.ericaskari.w4d5graph.bluetoothdeviceservice

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface IBluetoothDeviceServiceRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllItemsStream(): Flow<List<BluetoothDeviceService>>
    fun getAllItemsByDeviceId(id: String): Flow<List<BluetoothDeviceService>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: String): Flow<BluetoothDeviceService?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: BluetoothDeviceService)

    /**
     * Insert item in the data source
     */
    suspend fun syncItems(deviceId: String, vararg items: BluetoothDeviceService): Flow<List<BluetoothDeviceService>>

//    /**
//     * Insert item in the data source
//     */
//    suspend fun insertItems(vararg items: Actor)
//
    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: BluetoothDeviceService)
//
//    /**
//     * Update item in the data source
//     */
//    suspend fun updateItem(item: Actor)
}