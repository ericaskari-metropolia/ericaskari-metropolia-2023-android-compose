package com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


/**
 * @author Mohammad Askari
 */
class BluetoothDeviceServiceCharacteristicDescriptorRepository(private val itemDao: BluetoothDeviceServiceCharacteristicDescriptorDao) :
    IBluetoothDeviceServiceCharacteristicDescriptorRepository {
    override suspend fun deleteItem(item: BluetoothDeviceServiceCharacteristicDescriptor) = itemDao.deleteItem(item)

    override fun getAllItemsByCharacteristicId(id: String) = itemDao.getAllItemsByCharacteristicId(id)

    override suspend fun insertItem(item: BluetoothDeviceServiceCharacteristicDescriptor) = itemDao.insert(item)
    override fun getAllItemsStream() = itemDao.getAllItems()
    override suspend fun syncItems(
        characteristicId: String,
        vararg items: BluetoothDeviceServiceCharacteristicDescriptor
    ): Flow<List<BluetoothDeviceServiceCharacteristicDescriptor>> {
        val itemsIds = items.map { it.id }
        val allItems = getAllItemsByCharacteristicId(characteristicId)
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