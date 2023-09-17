package com.ericaskari.w4d5bluetooth.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ericaskari.w4d5bluetooth.AppBluetoothDeviceListItem
import com.ericaskari.w4d5bluetooth.bluetoothsearch.BluetoothDevice

@Composable
fun AppBluetoothDeviceList(data: List<BluetoothDevice>, modifier: Modifier = Modifier, onClick: (id: String) -> Unit) {
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        items(data) {
            AppBluetoothDeviceListItem(device = it) { id ->
                onClick(id)
            }
        }
    }

}