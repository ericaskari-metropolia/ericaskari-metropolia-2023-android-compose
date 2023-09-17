package com.ericaskari.w4d5bluetooth.bluetoothdeviceservice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


/**
 * @author Mohammad Askari
 */
class BluetoothDeviceServiceRepository(private val itemDao: BluetoothDeviceServiceDao) : IBluetoothDeviceServiceRepository {
    override suspend fun deleteItem(item: BluetoothDeviceService) = itemDao.deleteItem(item)
    override suspend fun insertItem(item: BluetoothDeviceService) = itemDao.insert(item)
    override fun getAllItemsStream() = itemDao.getAllItems()
    override fun getItemStream(id: String) = itemDao.getItem(id)
    override fun getAllItemsByDeviceId(id: String) = itemDao.getAllItemsByDeviceId(id)

    override suspend fun syncItems(deviceId: String, vararg items: BluetoothDeviceService): Flow<List<BluetoothDeviceService>> {
        val itemsIds = items.map { it.id }
        val allItems = getAllItemsByDeviceId(deviceId)
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