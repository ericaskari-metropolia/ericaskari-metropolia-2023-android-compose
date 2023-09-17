package com.ericaskari.w4d5bluetooth.presentation.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ericaskari.w4d5bluetooth.application.data.AppViewModelProvider
import com.ericaskari.w4d5bluetooth.bluetoothconnect.AppBluetoothConnectViewModel
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservice.BluetoothDeviceServiceViewModel
import com.ericaskari.w4d5bluetooth.bluetoothsearch.BluetoothDeviceViewModel
import com.ericaskari.w4d5bluetooth.presentation.components.AppBluetoothDeviceServiceList

@Composable
fun DeviceDetailsPage(
    navHostController: NavHostController,
    deviceId: String?,
    bluetoothDeviceViewModel: BluetoothDeviceViewModel = viewModel(factory = AppViewModelProvider.Factory),
    bluetoothDeviceServiceViewModel: BluetoothDeviceServiceViewModel = viewModel(factory = AppViewModelProvider.Factory),
    appBluetoothConnectViewModel: AppBluetoothConnectViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val connectMessage = appBluetoothConnectViewModel.connectMessage.collectAsState("unknown")

    Column {
        Button(onClick = { appBluetoothConnectViewModel.connect(deviceId!!) }) {
            Text(text = "Connect")
        }
        Button(onClick = { appBluetoothConnectViewModel.close() }) {
            Text(text = "Close")
        }
        Row {
            Text(text = "Status: ")
            Text(text = connectMessage.value.toString())
        }

        val item = bluetoothDeviceViewModel.getItemStream(deviceId!!).collectAsState(initial = null)

        item.value?.let {
            val services = bluetoothDeviceServiceViewModel.getAllItemsByDeviceId(it.address).collectAsState(listOf())
            AppBluetoothDeviceServiceList(
                serviceList = services.value
            ) { serviceId ->
                navHostController.navigate("devices/$deviceId/services/$serviceId")
            }
        }
    }
}