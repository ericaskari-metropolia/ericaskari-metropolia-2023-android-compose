package com.ericaskari.w4d5graph.presentation.pages

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ericaskari.w4d5graph.application.data.AppViewModelProvider
import com.ericaskari.w4d5graph.bluetooth.AppBluetoothViewModel
import com.ericaskari.w4d5graph.bluetoothsearch.BluetoothDeviceViewModel
import com.ericaskari.w4d5graph.presentation.components.AppBluetoothDeviceList

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}


@Composable
fun DevicesPage(
    navHostController: NavHostController,
    appBluetoothViewModel: AppBluetoothViewModel = viewModel(factory = AppViewModelProvider.Factory),
    bluetoothDeviceViewModel: BluetoothDeviceViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val context = LocalContext.current

    val isScanning = appBluetoothViewModel.isScanning.collectAsState()
    val allItemsStream = bluetoothDeviceViewModel.allItemsStream.collectAsState(emptyList())
    val bluetoothAdapterState = appBluetoothViewModel.bluetoothAdapterState.collectAsState(0)
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted: Boolean ->
                if (isGranted) {

                }
            })

    Column {
        Text(text = "DevicesPage")
        Text(text = "isScanning: ${isScanning.value}")
        Text(text = "bluetoothAdapterState: ${bluetoothAdapterState.value}")
        Column {
            Row {
                Button(onClick = { appBluetoothViewModel.askToTurnBluetoothOn() }) {
                    Text(text = "Allow bluetooth")
                }
                Button(onClick = {
                    if (!hasLocationPermission(context)) {
                        // Request location permission
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }) {
                    Text(text = "Allow location")
                }
            }
            Row {
                Button(onClick = { appBluetoothViewModel.startScan() }) {
                    Text(text = "Start Scan")
                }
                Button(onClick = { appBluetoothViewModel.stopScan() }) {
                    Text(text = "Stop Scan")
                }
            }
        }
        AppBluetoothDeviceList(allItemsStream.value) {
            navHostController.navigate("devices/${it}")

        }
    }
}
