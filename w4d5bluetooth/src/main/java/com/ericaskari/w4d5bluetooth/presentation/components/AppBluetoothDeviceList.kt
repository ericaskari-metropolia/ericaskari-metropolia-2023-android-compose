package com.ericaskari.w4d5bluetooth.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ericaskari.w4d5bluetooth.bluetoothsearch.BluetoothDevice
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppBluetoothDeviceList(data: List<BluetoothDevice>, modifier: Modifier = Modifier, onClick: (id: String) -> Unit) {
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        items(data) { device ->

            ListItem(
                modifier = Modifier
                    .clickable { onClick(device.address) },

                overlineContent = { Text(device.address) },
                headlineContent = { device.deviceName?.let { Text(it) } },
                supportingContent = { Text(device.rssi.toString() + "dBm") },
                leadingContent = {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = "AccountCircle",
                    )
                },
                trailingContent = {
                    device.lastSeen?.let {
                        Text(
                            SimpleDateFormat(
                                "MM/dd/yy h:mm:ss ",
                                Locale.US
                            ).format(Date(device.lastSeen))
                        )
                    }
                }
            )
        }
    }

}