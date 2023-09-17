package com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface IBluetoothDeviceServiceCharacteristicDescriptorRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllItemsStream(): Flow<List<BluetoothDeviceServiceCharacteristicDescriptor>>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: BluetoothDeviceServiceCharacteristicDescriptor)


    fun getAllItemsByCharacteristicId(id: String): Flow<List<BluetoothDeviceServiceCharacteristicDescriptor>>

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: BluetoothDeviceServiceCharacteristicDescriptor)

    /**
     * Insert item in the data source
     */
    suspend fun syncItems(vararg items: BluetoothDeviceServiceCharacteristicDescriptor): Flow<List<BluetoothDeviceServiceCharacteristicDescriptor>>

}