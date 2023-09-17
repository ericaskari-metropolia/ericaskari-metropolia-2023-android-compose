package com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


/**
 * @author Mohammad Askari
 */
class BluetoothDeviceServiceCharacteristicRepository(private val itemDao: BluetoothDeviceServiceCharacteristicDao) :
    IBluetoothDeviceServiceCharacteristicRepository {

    override suspend fun deleteItem(item: BluetoothDeviceServiceCharacteristic) = itemDao.deleteItem(item)
    override suspend fun insertItem(item: BluetoothDeviceServiceCharacteristic) = itemDao.insert(item)
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
        return itemDao.getAllItems()
    }
}