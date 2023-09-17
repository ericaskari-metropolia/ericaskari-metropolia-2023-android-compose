package com.ericaskari.w4d5bluetooth.presentation.components

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ericaskari.w4d5bluetooth.application.data.AppViewModelProvider
import com.ericaskari.w4d5bluetooth.bluetoothconnect.AppBluetoothConnectViewModel
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristic
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppBluetoothDeviceServiceCharacteristicDescriptorListItem(
    characteristic: BluetoothDeviceServiceCharacteristic,
    descriptor: BluetoothDeviceServiceCharacteristicDescriptor,
    appBluetoothConnectViewModel: AppBluetoothConnectViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onClick: (id: String) -> Unit,
) {

    ListItem(
        overlineContent = { Text("Descriptor") },
        headlineContent = { Text(descriptor.id) },
        supportingContent = {
            Column {
                if (descriptor.id.equals("00002902-0000-1000-8000-00805f9b34fb", ignoreCase = true)) {
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                appBluetoothConnectViewModel.writeDescriptor(
                                    serviceId = characteristic.serviceId,
                                    characteristicId = characteristic.id,
                                    descriptorId = descriptor.id,
                                    value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                )
                            }
                        },
                    ) {
                        Text(text = "ENABLE_NOTIFICATION_VALUE")
                    }
                }
                if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE > 0) {
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                appBluetoothConnectViewModel.writeDescriptor(
                                    serviceId = characteristic.serviceId,
                                    characteristicId = characteristic.id,
                                    descriptorId = descriptor.id,
                                    value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                                )
                            }
                        },
                    ) {
                        Text(text = "ENABLE_INDICATION_VALUE")
                    }
                }
            }
        }
    )
}