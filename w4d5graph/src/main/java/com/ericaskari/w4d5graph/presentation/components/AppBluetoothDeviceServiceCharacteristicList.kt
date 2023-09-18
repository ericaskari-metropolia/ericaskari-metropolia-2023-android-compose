package com.ericaskari.w4d5graph.presentation.components

import android.bluetooth.BluetoothGattCharacteristic
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ericaskari.w4d5graph.application.data.AppViewModelProvider
import com.ericaskari.w4d5graph.bluetooth.models.CharacteristicPermission
import com.ericaskari.w4d5graph.bluetooth.models.CharacteristicProperty
import com.ericaskari.w4d5graph.bluetooth.models.CharacteristicWriteType
import com.ericaskari.w4d5graph.bluetoothconnect.AppBluetoothConnectViewModel
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristic
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptorViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppBluetoothDeviceServiceCharacteristicList(
    characteristicList: List<BluetoothDeviceServiceCharacteristic>,
    modifier: Modifier = Modifier,
    bluetoothDeviceServiceCharacteristicDescriptorViewModel: BluetoothDeviceServiceCharacteristicDescriptorViewModel = viewModel(factory = AppViewModelProvider.Factory),
    appBluetoothConnectViewModel: AppBluetoothConnectViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onClick: (id: String) -> Unit
) {

    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        items(characteristicList) { characteristic ->

            val descriptors =
                bluetoothDeviceServiceCharacteristicDescriptorViewModel.getAllItemsByCharacteristicId(characteristic.id)
                    .collectAsState(listOf())

            ListItem(
                modifier = Modifier
                    .clickable { onClick(characteristic.id) },
                overlineContent = { Text("Characteristic") },
                headlineContent = { Text(characteristic.id) },
                supportingContent = {
                    Column {
                        Text("permissions:")
                        Text(CharacteristicPermission.getAll(characteristic.permissions).toString())
                        Text("properties:")
                        Text(CharacteristicProperty.getAll(characteristic.properties).toString())
                        Text("writeTypes:")
                        Text(CharacteristicWriteType.getAll(characteristic.writeType).toString())
                        Text("actions:")
                        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
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
                                Text(text = "Enable Notification")
                            }
                        }
                    }
                },
            )
        }
    }
}
