package com.ericaskari.w4d5bluetooth.presentation.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ericaskari.w4d5bluetooth.application.data.AppViewModelProvider
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristicViewModel
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptorViewModel
import com.ericaskari.w4d5bluetooth.presentation.components.AppBluetoothDeviceServiceCharacteristicDescriptorList

@Composable
fun DeviceServiceCharacteristicDetailsPage(
    navHostController: NavHostController,
    deviceId: String?,
    serviceId: String?,
    characteristicId: String?,
    modifier: Modifier = Modifier,
    serviceCharacteristicViewModel: BluetoothDeviceServiceCharacteristicViewModel = viewModel(factory = AppViewModelProvider.Factory),
    bluetoothDeviceServiceCharacteristicDescriptorViewModel: BluetoothDeviceServiceCharacteristicDescriptorViewModel = viewModel(factory = AppViewModelProvider.Factory),

    ) {

    val queriedCharacteristic = serviceId?.let { serviceCharacteristicViewModel.getItemStream(it).collectAsState(null) }

    queriedCharacteristic?.value?.let { characteristic ->
        val descriptors = bluetoothDeviceServiceCharacteristicDescriptorViewModel.getAllItemsByCharacteristicId(characteristic.id)
            .collectAsState(listOf())

        Column {
            Text(text = "DeviceServiceCharacteristicDetailsPage")
            ListItem(
                overlineContent = { Text("Characteristic") },
                headlineContent = { Text(characteristic.id) },
            )
            AppBluetoothDeviceServiceCharacteristicDescriptorList(
                data = descriptors.value,
                characteristic = characteristic
            ) {

            }
        }
    }


}