package com.ericaskari.w4d5graph.presentation.pages

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
import com.ericaskari.w4d5graph.application.data.AppViewModelProvider
import com.ericaskari.w4d5graph.bluetooth.models.toInteger
import com.ericaskari.w4d5graph.bluetoothconnect.AppBluetoothConnectViewModel
import com.ericaskari.w4d5graph.bluetoothdeviceservice.BluetoothDeviceServiceViewModel
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristicViewModel
import com.ericaskari.w4d5graph.presentation.components.AppBluetoothDeviceServiceCharacteristicList
import com.ericaskari.w4d5graph.presentation.components.BluetoothDeviceServiceValueChart

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
            BluetoothDeviceServiceValueChart()
            AppBluetoothDeviceServiceCharacteristicList(
                characteristicList = characteristic.value
            ) { characteristicId ->
                navHostController.navigate("devices/$deviceId/services/$serviceId/characteristic/$characteristicId")
            }
        }
    }


}