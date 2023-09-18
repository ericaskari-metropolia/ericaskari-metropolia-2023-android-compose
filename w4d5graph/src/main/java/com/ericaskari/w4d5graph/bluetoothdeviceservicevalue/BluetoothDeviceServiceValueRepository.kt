package com.ericaskari.w4d5graph.bluetoothdeviceservicevalue


/**
 * @author Mohammad Askari
 */
class BluetoothDeviceServiceValueRepository(private val itemDao: BluetoothDeviceServiceValueDao) : IBluetoothDeviceServiceValueRepository {
    override fun insertItem(item: BluetoothDeviceServiceValue) = itemDao.insert(item)
    override fun deleteItem(item: BluetoothDeviceServiceValue) = itemDao.deleteItem(item)
    override fun getAllItemsStream() = itemDao.getAllItemsStream()
    override fun getItemStream(id: Int) = itemDao.getItemStream(id)
    override fun getAllItemsByDeviceIdStream(id: String) = itemDao.getAllItemsByDeviceIdStream(id)
    override fun getAllItemsByServiceIdStream(deviceId: String, serviceId: String) =
        itemDao.getAllItemsByServiceIdStream(deviceId = deviceId, serviceId = serviceId)


}