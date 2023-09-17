package com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface IBluetoothDeviceServiceCharacteristicRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllItemsStream(): Flow<List<BluetoothDeviceServiceCharacteristic>>
//
//    /**
//     * Retrieve an item from the given data source that matches with the [id].
//     */
//    fun getItemStream(id: String): Flow<Actor?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: BluetoothDeviceServiceCharacteristic)

    fun getAllItemsByServiceId(id: String): Flow<List<BluetoothDeviceServiceCharacteristic>>

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: BluetoothDeviceServiceCharacteristic)

    /**
     * Insert item in the data source
     */
    suspend fun syncItems(
        serviceId: String,
        vararg items: BluetoothDeviceServiceCharacteristic,
    ): Flow<List<BluetoothDeviceServiceCharacteristic>>

}