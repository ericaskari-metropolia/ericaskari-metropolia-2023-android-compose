package com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


/**
 * @author Mohammad Askari
 */
class BluetoothDeviceServiceCharacteristicRepository(private val itemDao: BluetoothDeviceServiceCharacteristicDao) :
    IBluetoothDeviceServiceCharacteristicRepository {

    override suspend fun deleteItem(item: BluetoothDeviceServiceCharacteristic) = itemDao.deleteItem(item)
    override suspend fun insertItem(item: BluetoothDeviceServiceCharacteristic) = itemDao.insert(item)
    override fun getItemStream(id: String, serviceId: String) = itemDao.getItem(id, serviceId)

    override fun getAllItemsStream() = itemDao.getAllItems()
    override fun getAllItemsByServiceId(id: String) = itemDao.getAllItemsByServiceId(id)

    override suspend fun syncItems(
        serviceId: String,
        vararg items: BluetoothDeviceServiceCharacteristic,
    ): Flow<List<BluetoothDeviceServiceCharacteristic>> {
        val itemsIds = items.map { it.id }
        val allItems = getAllItemsByServiceId(serviceId)
        val list = allItems.first()

        val allItemsIds = list.map { it.id }

        val shouldUpdateItem = list.filter { itemsIds.contains(it.id) }
        val shouldDeleteItem = list.filter { !itemsIds.contains(it.id) }
        val shouldInsertItem = items.filter { !allItemsIds.contains(it.id) }
        shouldUpdateItem.forEach { insertItem(it) }
        shouldInsertItem.forEach { insertItem(it) }
        shouldDeleteItem.forEach { deleteItem(it) }
        return itemDao.getAllItems()
    }
}