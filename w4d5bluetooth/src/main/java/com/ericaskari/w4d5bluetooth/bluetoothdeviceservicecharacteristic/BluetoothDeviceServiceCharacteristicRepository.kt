package com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic


/**
 * @author Mohammad Askari
 */
class BluetoothDeviceServiceCharacteristicRepository(private val itemDao: BluetoothDeviceServiceCharacteristicDao) :
    IBluetoothDeviceServiceCharacteristicRepository {
    override suspend fun insertItem(item: BluetoothDeviceServiceCharacteristic) = itemDao.insert(item)
    override fun getAllItemsStream() = itemDao.getAllItems()

}