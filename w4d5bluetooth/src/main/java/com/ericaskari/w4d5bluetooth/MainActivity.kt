package com.ericaskari.w4d5bluetooth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ericaskari.w4d5bluetooth.application.MyApplication
import com.ericaskari.w4d5bluetooth.application.data.AppViewModelProvider
import com.ericaskari.w4d5bluetooth.bluetooth.AppBluetoothViewModel
import com.ericaskari.w4d5bluetooth.bluetoothconnect.AppBluetoothConnectViewModel
import com.ericaskari.w4d5bluetooth.bluetoothsearch.AppBluetoothSearch
import com.ericaskari.w4d5bluetooth.bluetoothsearch.AppBluetoothSearchViewModel
import com.ericaskari.w4d5bluetooth.ui.theme.FirstComposeAppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.lifecycle.addObserver((application as MyApplication).initializeAppBluetoothObserver(this))

        setContent {
            FirstComposeAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ApplicationContent()
                }
            }
        }
    }
}

@Composable
fun ApplicationContent(
    appBluetoothViewModel: AppBluetoothViewModel = viewModel(factory = AppViewModelProvider.Factory),
    appBluetoothSearchViewModel: AppBluetoothSearchViewModel = viewModel(factory = AppViewModelProvider.Factory),
    appBluetoothConnectViewModel: AppBluetoothConnectViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val isScanning = appBluetoothViewModel.isScanning.collectAsState()
    val allItemsStream = appBluetoothSearchViewModel.allItemsStream.collectAsState(emptyList())
    val bluetoothAdapterState = appBluetoothViewModel.bluetoothAdapterState.collectAsState(0)
//    val services = wikiViewModel._services.collectAsState()

    Column {
        Text(text = "isScanning: ${isScanning.value}")
        Text(text = "bluetoothAdapterState: ${bluetoothAdapterState.value}")
        Row {
            Button(onClick = { appBluetoothViewModel.startScan() }) {
                Text(text = "Start Scan")
            }
            Button(onClick = { appBluetoothViewModel.stopScan() }) {
                Text(text = "Stop Scan")
            }
        }
        AppBluetoothSearchList(allItemsStream.value) {
            appBluetoothConnectViewModel.connect(it)
        }
    }
}

@Composable
fun AppBluetoothSearchList(data: List<AppBluetoothSearch>, modifier: Modifier = Modifier, onClick: (id: String) -> Unit) {
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        items(data) {
            AppBluetoothSearchListItem(data = it) { id ->
                onClick(id)
            }
        }
    }

}


@Composable
fun AppBluetoothSearchListItem(data: AppBluetoothSearch, onClick: (id: String) -> Unit) {
    ListItem(
        modifier = Modifier
            .clickable { onClick(data.address) },

        overlineContent = { Text(data.address) },
        headlineContent = { data.deviceName?.let { Text(it) } },
        supportingContent = { Text(data.rssi.toString() + "dBm") },
        leadingContent = {
            Icon(
                Icons.Filled.AccountCircle,
                contentDescription = "AccountCircle",
            )
        },
        trailingContent = { data.lastSeen?.let { Text(SimpleDateFormat("MM/dd/yy h:mm:ss ", Locale.US).format(Date(data.lastSeen))) } }
    )
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FirstComposeAppTheme {
        Greeting("Android")
    }
}

