package com.ericaskari.w4d5bluetooth.application.data

import android.content.Context
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservice.BluetoothDeviceServiceRepository
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservice.IBluetoothDeviceServiceRepository
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristicRepository
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic.IBluetoothDeviceServiceCharacteristicRepository
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptorRepository
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor.IBluetoothDeviceServiceCharacteristicDescriptorRepository
import com.ericaskari.w4d5bluetooth.bluetoothsearch.BluetoothDeviceRepository
import com.ericaskari.w4d5bluetooth.bluetoothsearch.IBluetoothDeviceRepository
import com.ericaskari.w4d5bluetooth.datasource.AppDatabase


/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val bluetoothDeviceRepository: IBluetoothDeviceRepository
    val bluetoothDeviceServiceRepository: IBluetoothDeviceServiceRepository
    val bluetoothDeviceServiceCharacteristicRepository: IBluetoothDeviceServiceCharacteristicRepository
    val bluetoothDeviceServiceCharacteristicDescriptorRepository: IBluetoothDeviceServiceCharacteristicDescriptorRepository
}

class AppDataContainer(private val context: Context) :
    AppContainer {

    override val bluetoothDeviceRepository: IBluetoothDeviceRepository by lazy {
        BluetoothDeviceRepository(AppDatabase.getInstance(context).bluetoothDeviceDao())
    }

    override val bluetoothDeviceServiceRepository: IBluetoothDeviceServiceRepository by lazy {
        BluetoothDeviceServiceRepository(AppDatabase.getInstance(context).bluetoothDeviceServiceDao())
    }

    override val bluetoothDeviceServiceCharacteristicRepository: IBluetoothDeviceServiceCharacteristicRepository by lazy {
        BluetoothDeviceServiceCharacteristicRepository(AppDatabase.getInstance(context).bluetoothDeviceServiceCharacteristicDao())
    }

    override val bluetoothDeviceServiceCharacteristicDescriptorRepository: IBluetoothDeviceServiceCharacteristicDescriptorRepository by lazy {
        BluetoothDeviceServiceCharacteristicDescriptorRepository(
            AppDatabase.getInstance(context).bluetoothDeviceServiceCharacteristicDescriptorDao()
        )
    }

}