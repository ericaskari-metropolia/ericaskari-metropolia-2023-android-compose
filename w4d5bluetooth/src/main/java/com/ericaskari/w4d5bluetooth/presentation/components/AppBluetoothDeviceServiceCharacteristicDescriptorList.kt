package com.ericaskari.w4d5bluetooth.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ericaskari.w4d5bluetooth.AppBluetoothDeviceServiceCharacteristicDescriptorListItem
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristic
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptor

@Composable
fun AppBluetoothDeviceServiceCharacteristicDescriptorList(
    characteristic: BluetoothDeviceServiceCharacteristic,
    data: List<BluetoothDeviceServiceCharacteristicDescriptor>,
    modifier: Modifier = Modifier,
    onClick: (id: String) -> Unit
) {
    data.forEach {
        AppBluetoothDeviceServiceCharacteristicDescriptorListItem(
            descriptor = it,
            characteristic = characteristic
        ) { id ->
        }
    }
}