package com.ericaskari.w4d5bluetooth.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ericaskari.w4d5bluetooth.application.data.AppViewModelProvider
import com.ericaskari.w4d5bluetooth.bluetoothconnect.AppBluetoothConnectViewModel
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristic
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppBluetoothDeviceServiceCharacteristicDescriptorList(
    characteristic: BluetoothDeviceServiceCharacteristic,
    data: List<BluetoothDeviceServiceCharacteristicDescriptor>,
    modifier: Modifier = Modifier,
    appBluetoothConnectViewModel: AppBluetoothConnectViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onClick: (id: String) -> Unit
) {
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        items(data) { descriptor ->
            ListItem(
                overlineContent = { Text("Descriptor") },
                headlineContent = { Text(descriptor.id) },
                supportingContent = {
                    Column {
                        if (descriptor.id.equals("00002902-0000-1000-8000-00805f9b34fb", ignoreCase = true)) {
                            Button(
                                onClick = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        appBluetoothConnectViewModel.enableNotificationsAndIndications(
                                            deviceId = characteristic.deviceId,
                                            serviceId = characteristic.serviceId,
                                            characteristicId = characteristic.id,
                                        )
                                    }
                                },
                            ) {
                                Text(text = "ENABLE_NOTIFICATION_VALUE")
                            }
                        }
                    }
                }
            )
        }
    }
}