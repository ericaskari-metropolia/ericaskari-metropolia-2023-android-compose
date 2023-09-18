package com.ericaskari.w4d5graph.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ericaskari.w4d5graph.bluetoothdeviceservice.BluetoothDeviceService

@Composable
fun AppBluetoothDeviceServiceList(
    serviceList: List<BluetoothDeviceService>,
    modifier: Modifier = Modifier,
    onClick: (id: String) -> Unit
) {
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        items(serviceList) { service ->
            ListItem(
                overlineContent = { Text("Service") },
                headlineContent = { Text(service.viewName ?: service.id) },
                modifier = Modifier
                    .clickable { onClick(service.id) },
            )
        }
    }
}
