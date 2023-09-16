package com.ericaskari.w4d5bluetooth.bluetoothsearch

/**
 * @author Mohammad Askari
 */
class AppBluetoothSearchRepository(private val itemDao: AppBluetoothSearchDao) : IAppBluetoothSearchRepository {
    override suspend fun insertItem(item: AppBluetoothSearch) = itemDao.insert(item)
    override fun getAllItemsStream() = itemDao.getAllItems()

}