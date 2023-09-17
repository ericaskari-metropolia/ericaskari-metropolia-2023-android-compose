package com.ericaskari.w4d5bluetooth.bluetoothsearch


/**
 * @author Mohammad Askari
 */
class BluetoothDeviceRepository(private val dao: BluetoothDeviceDao) : IBluetoothDeviceRepository {
    override suspend fun insertItem(item: BluetoothDevice) = dao.insert(item)
    override fun getAllItemsStream() = dao.getAllItems()
    override suspend fun deleteNotSeen() = dao.deleteNotSeen()
    override fun getItemStream(id: String) = dao.getItem(id)

}