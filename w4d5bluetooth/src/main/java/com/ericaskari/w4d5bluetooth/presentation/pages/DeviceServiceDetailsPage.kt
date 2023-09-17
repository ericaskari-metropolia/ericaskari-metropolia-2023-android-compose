package com.ericaskari.w4d5bluetooth.presentation.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ericaskari.w4d5bluetooth.application.data.AppViewModelProvider
import com.ericaskari.w4d5bluetooth.bluetooth.models.toInteger
import com.ericaskari.w4d5bluetooth.bluetoothconnect.AppBluetoothConnectViewModel
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservice.BluetoothDeviceServiceViewModel
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristicViewModel
import com.ericaskari.w4d5bluetooth.presentation.components.AppBluetoothDeviceServiceCharacteristicList

@Composable
fun DeviceServiceDetailsPage(
    navHostController: NavHostController,
    deviceId: String?,
    serviceId: String?,
    modifier: Modifier = Modifier,
    bluetoothDeviceServiceViewModel: BluetoothDeviceServiceViewModel = viewModel(factory = AppViewModelProvider.Factory),
    bluetoothDeviceServiceCharacteristicViewModel: BluetoothDeviceServiceCharacteristicViewModel = viewModel(factory = AppViewModelProvider.Factory),
    appBluetoothConnectViewModel: AppBluetoothConnectViewModel = viewModel(factory = AppViewModelProvider.Factory),

    ) {

    val queriedService = serviceId?.let { bluetoothDeviceServiceViewModel.getItemStream(it).collectAsState(null) }


    queriedService?.value?.let { service ->
        val characteristic = bluetoothDeviceServiceCharacteristicViewModel.getAllItemsByServiceId(service.id).collectAsState(listOf())
        val output = appBluetoothConnectViewModel.output.collectAsState(initial = null)
        Column {
            Text(text = "DeviceServiceDetailsPage")
            ListItem(
                overlineContent = { Text("Service") },
                headlineContent = { Text(service.viewName ?: service.id) },
            )
            ListItem(
                modifier = Modifier.background(Color.Green),
                overlineContent = { Text("Value") },
                headlineContent = { output.value?.let { Text(text = it.toInteger().toString()) } },
            )

            AppBluetoothDeviceServiceCharacteristicList(
                characteristicList = characteristic.value
            ) { characteristicId ->
                navHostController.navigate("devices/$deviceId/services/$serviceId/characteristic/$characteristicId")
            }
        }
    }


}