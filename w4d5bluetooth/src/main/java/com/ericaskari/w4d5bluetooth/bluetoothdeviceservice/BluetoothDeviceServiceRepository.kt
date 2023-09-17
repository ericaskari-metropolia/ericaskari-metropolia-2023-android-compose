package com.ericaskari.w4d5bluetooth.bluetoothdeviceservice

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest


/**
 * @author Mohammad Askari
 */
class BluetoothDeviceServiceRepository(private val itemDao: BluetoothDeviceServiceDao) : IBluetoothDeviceServiceRepository {
    override suspend fun deleteItem(item: BluetoothDeviceService) = itemDao.deleteItem(item)
    override suspend fun insertItem(item: BluetoothDeviceService) = itemDao.insert(item)
    override fun getAllItemsStream() = itemDao.getAllItems()
    override suspend fun syncItems(vararg items: BluetoothDeviceService): Flow<List<BluetoothDeviceService>> {
        val itemsIds = items.map { it.id }
        val allItems = getAllItemsStream()
        allItems.collectLatest { list ->
            val allItemsIds = list.map { it.id }

            val shouldUpdateItem = list.find { itemsIds.contains(it.id) }
            val shouldDeleteItem = list.find { !itemsIds.contains(it.id) }
            val shouldInsertItem = items.find { !allItemsIds.contains(it.id) }
            if (shouldInsertItem != null) {
                insertItem(shouldInsertItem)
            }
            if (shouldUpdateItem != null) {
                insertItem(shouldUpdateItem)
            }
            if (shouldDeleteItem != null) {
                deleteItem(shouldDeleteItem)
            }
        }
        return itemDao.getAllItems()
    }

}