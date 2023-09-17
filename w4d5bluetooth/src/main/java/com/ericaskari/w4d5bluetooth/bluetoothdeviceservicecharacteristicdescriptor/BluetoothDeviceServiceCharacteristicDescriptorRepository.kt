package com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor


/**
 * @author Mohammad Askari
 */
class BluetoothDeviceServiceCharacteristicDescriptorRepository(private val itemDao: BluetoothDeviceServiceCharacteristicDescriptorDao) :
    IBluetoothDeviceServiceCharacteristicDescriptorRepository {
    override suspend fun insertItem(item: BluetoothDeviceServiceCharacteristicDescriptor) = itemDao.insert(item)
    override fun getAllItemsStream() = itemDao.getAllItems()

}