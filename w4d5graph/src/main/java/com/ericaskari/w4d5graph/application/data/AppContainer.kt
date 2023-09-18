package com.ericaskari.w4d5graph.application.data

import android.content.Context
import com.ericaskari.w4d5graph.bluetoothdeviceservice.BluetoothDeviceServiceRepository
import com.ericaskari.w4d5graph.bluetoothdeviceservice.IBluetoothDeviceServiceRepository
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristicRepository
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristic.IBluetoothDeviceServiceCharacteristicRepository
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptorRepository
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristicdescriptor.IBluetoothDeviceServiceCharacteristicDescriptorRepository
import com.ericaskari.w4d5graph.bluetoothdeviceservicevalue.BluetoothDeviceServiceValueRepository
import com.ericaskari.w4d5graph.bluetoothdeviceservicevalue.IBluetoothDeviceServiceValueRepository
import com.ericaskari.w4d5graph.bluetoothsearch.BluetoothDeviceRepository
import com.ericaskari.w4d5graph.bluetoothsearch.IBluetoothDeviceRepository
import com.ericaskari.w4d5graph.datasource.AppDatabase
import com.ericaskari.w4d5graph.network.RetrofitFactory
import com.ericaskari.w4d5graph.nordicsemiconductordatabase.BluetoothServiceInfoRepository
import com.ericaskari.w4d5graph.nordicsemiconductordatabase.IBluetoothServiceInfoRepository


/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val bluetoothDeviceRepository: IBluetoothDeviceRepository
    val bluetoothDeviceServiceRepository: IBluetoothDeviceServiceRepository
    val bluetoothDeviceServiceCharacteristicRepository: IBluetoothDeviceServiceCharacteristicRepository
    val bluetoothDeviceServiceCharacteristicDescriptorRepository: IBluetoothDeviceServiceCharacteristicDescriptorRepository
    val bluetoothServiceInfoRepository: IBluetoothServiceInfoRepository
    val bluetoothDeviceServiceValueRepository: IBluetoothDeviceServiceValueRepository
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

    override val bluetoothServiceInfoRepository: IBluetoothServiceInfoRepository by lazy {
        BluetoothServiceInfoRepository(
            AppDatabase.getInstance(context).bluetoothServiceInfoDao(),
            RetrofitFactory.makeBluetoothServiceInfoApi()
        )
    }

    override val bluetoothDeviceServiceValueRepository: IBluetoothDeviceServiceValueRepository by lazy {
        BluetoothDeviceServiceValueRepository(
            AppDatabase.getInstance(context).bluetoothDeviceServiceValueDao(),
        )
    }

}