package com.ericaskari.w4d5graph.bluetoothdeviceservicevalue

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface IBluetoothDeviceServiceValueRepository {
    fun insertItem(item: BluetoothDeviceServiceValue)
    fun deleteItem(item: BluetoothDeviceServiceValue)
    fun getAllItemsStream(): Flow<List<BluetoothDeviceServiceValue>>
    fun getItemStream(id: Int): Flow<BluetoothDeviceServiceValue?>
    fun getAllItemsByDeviceIdStream(id: String): Flow<List<BluetoothDeviceServiceValue>>
    fun getAllItemsByServiceIdStream(deviceId: String, serviceId: String): Flow<List<BluetoothDeviceServiceValue>>

}