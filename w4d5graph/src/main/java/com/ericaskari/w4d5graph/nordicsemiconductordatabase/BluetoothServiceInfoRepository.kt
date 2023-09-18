package com.ericaskari.w4d5graph.nordicsemiconductordatabase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.await

class BluetoothServiceInfoRepository(private val itemDao: BluetoothServiceInfoDao, private val api: BluetoothServiceInfoApi) :
    IBluetoothServiceInfoRepository {
    override suspend fun deleteItem(item: BluetoothServiceInfo) = itemDao.deleteItem(item)
    override suspend fun insertItem(item: BluetoothServiceInfo) = itemDao.insert(item)
    override fun getAllItemsStream() = itemDao.getAllItems()
    override suspend fun syncItems(): Flow<List<BluetoothServiceInfo>> {
        val fetchedItems = api.getBluetoothServices().await()
        val fetchedItemsIds = fetchedItems.map { it.identifier }
        val list = getAllItemsStream().first()

        val allItemsIds = list.map { it.identifier }

        val shouldUpdateItem = list.filter { fetchedItemsIds.contains(it.identifier) }
        val shouldDeleteItem = list.filter { !fetchedItemsIds.contains(it.identifier) }
        val shouldInsertItem = fetchedItems.filter { !allItemsIds.contains(it.identifier) }
        shouldUpdateItem.forEach { insertItem(it) }
        shouldInsertItem.forEach { insertItem(it) }
        shouldDeleteItem.forEach { deleteItem(it) }
        return itemDao.getAllItems()
    }

}