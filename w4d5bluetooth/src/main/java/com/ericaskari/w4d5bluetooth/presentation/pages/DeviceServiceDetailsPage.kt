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
import com.ericaskari.w4d5bluetooth.bluetooth.models.toGss
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservice.BluetoothDeviceServiceViewModel
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristicViewModel
import com.ericaskari.w4d5bluetooth.nordicsemiconductordatabase.BluetoothServiceInfoViewModel
import com.ericaskari.w4d5bluetooth.presentation.components.AppBluetoothDeviceServiceCharacteristicList
import java.util.UUID

@Composable
fun DeviceServiceDetailsPage(
    navHostController: NavHostController,
    deviceId: String?,
    serviceId: String?,
    modifier: Modifier = Modifier,
    bluetoothDeviceServiceViewModel: BluetoothDeviceServiceViewModel = viewModel(factory = AppViewModelProvider.Factory),
    bluetoothDeviceServiceCharacteristicViewModel: BluetoothDeviceServiceCharacteristicViewModel = viewModel(factory = AppViewModelProvider.Factory),
    bluetoothServiceInfoViewModel: BluetoothServiceInfoViewModel = viewModel(factory = AppViewModelProvider.Factory),

    ) {

    val queriedService = serviceId?.let { bluetoothDeviceServiceViewModel.getItemStream(it).collectAsState(null) }

    val bluetoothServiceInfoList = bluetoothServiceInfoViewModel.allItemsStream.collectAsState(initial = listOf())

    queriedService?.value?.let { service ->
        val characteristic = bluetoothDeviceServiceCharacteristicViewModel.getAllItemsByServiceId(service.id).collectAsState(listOf())

        val name = bluetoothServiceInfoList.value.find { it.uuid == UUID.fromString(service.id).toGss() }

        Column {
            ListItem(
                overlineContent = { Text("Service") },
                headlineContent = { Text(service.viewName ?: service.id) },
            )
            AppBluetoothDeviceServiceCharacteristicList(
                characteristicList = characteristic.value
            ) {

            }
        }
    }


}