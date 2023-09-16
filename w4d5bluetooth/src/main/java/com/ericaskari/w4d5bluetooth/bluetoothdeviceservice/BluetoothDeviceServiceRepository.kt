package com.ericaskari.w4d5bluetooth.bluetoothdeviceservice


/**
 * @author Mohammad Askari
 */
class BluetoothDeviceServiceRepository(private val itemDao: BluetoothDeviceServiceDao) : IBluetoothDeviceServiceRepository {
    override suspend fun insertItem(item: BluetoothDeviceService) = itemDao.insert(item)
    override fun getAllItemsStream() = itemDao.getAllItems()

}