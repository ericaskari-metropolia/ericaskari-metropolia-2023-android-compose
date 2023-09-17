package com.ericaskari.w4d5bluetooth

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
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
import com.ericaskari.w4d5bluetooth.bluetooth.models.CharacteristicDescriptorPermission
import com.ericaskari.w4d5bluetooth.bluetooth.models.CharacteristicProperty
import com.ericaskari.w4d5bluetooth.bluetooth.models.CharacteristicWriteType
import com.ericaskari.w4d5bluetooth.bluetoothconnect.AppBluetoothConnectViewModel
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservice.BluetoothDeviceService
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservice.BluetoothDeviceServiceViewModel
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristic
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristicViewModel
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptor
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptorViewModel
import com.ericaskari.w4d5bluetooth.bluetoothsearch.BluetoothDevice
import com.ericaskari.w4d5bluetooth.bluetoothsearch.BluetoothDeviceViewModel
import com.ericaskari.w4d5bluetooth.ui.theme.FirstComposeAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    companion object {
        private val parentJob = Job()
        private val coroutineScope = CoroutineScope(Dispatchers.Default + parentJob)
    }

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
    bluetoothDeviceViewModel: BluetoothDeviceViewModel = viewModel(factory = AppViewModelProvider.Factory),
    appBluetoothConnectViewModel: AppBluetoothConnectViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {

    val isScanning = appBluetoothViewModel.isScanning.collectAsState()
    val allItemsStream = bluetoothDeviceViewModel.allItemsStream.collectAsState(emptyList())
    val bluetoothAdapterState = appBluetoothViewModel.bluetoothAdapterState.collectAsState(0)
//    val services = wikiViewModel._services.collectAsState()

    Column {
        Text(text = "isScanning: ${isScanning.value}")
        Text(text = "bluetoothAdapterState: ${bluetoothAdapterState.value}")
        Row {
            Button(onClick = { appBluetoothViewModel.askToTurnBluetoothOn() }) {
                Text(text = "Allow bluetooth")
            }
            Button(onClick = { appBluetoothViewModel.startScan() }) {
                Text(text = "Start Scan")
            }
            Button(onClick = { appBluetoothViewModel.stopScan() }) {
                Text(text = "Stop Scan")
            }
        }
        Row {
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    appBluetoothConnectViewModel.readAll()
                }
            }) {
                Text(text = "readAll")
            }
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    appBluetoothConnectViewModel.readCharacteristic()
                }
            }) {
                Text(text = "readCharacteristic")
            }
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    appBluetoothConnectViewModel.readDescriptor()
                }
            }) {
                Text(text = "readDescriptor")
            }
        }
        AppBluetoothDeviceList(allItemsStream.value) {
            appBluetoothConnectViewModel.connect(it)
        }
    }
}

@Composable
fun AppBluetoothDeviceList(data: List<BluetoothDevice>, modifier: Modifier = Modifier, onClick: (id: String) -> Unit) {
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        items(data) {
            AppBluetoothDeviceListItem(data = it) { id ->
                onClick(id)
            }
        }
    }

}


@Composable
fun AppBluetoothDeviceListItem(
    bluetoothDeviceServiceViewModel: BluetoothDeviceServiceViewModel = viewModel(factory = AppViewModelProvider.Factory),
    data: BluetoothDevice,
    onClick: (id: String) -> Unit
) {
    val services = bluetoothDeviceServiceViewModel.getAllItemsByDeviceId(data.address).collectAsState(listOf())

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
    AppBluetoothDeviceServiceList(
        data = services.value
    ) {

    }
}

@Composable
fun AppBluetoothDeviceServiceList(
    data: List<BluetoothDeviceService>,
    modifier: Modifier = Modifier,
    onClick: (id: String) -> Unit
) {
    data.forEach {
        AppBluetoothDeviceServiceListItem(data = it) { id ->
        }
    }
}


@Composable
fun AppBluetoothDeviceServiceListItem(
    bluetoothDeviceServiceCharacteristicViewModel: BluetoothDeviceServiceCharacteristicViewModel = viewModel(factory = AppViewModelProvider.Factory),
    data: BluetoothDeviceService,
    onClick: (id: String) -> Unit
) {
    val characteristic = bluetoothDeviceServiceCharacteristicViewModel.getAllItemsByServiceId(data.id).collectAsState(listOf())

    ListItem(
        overlineContent = { Text("Service") },
        headlineContent = { Text(data.id) },
    )
    AppBluetoothDeviceServiceCharacteristicList(
        data = characteristic.value
    ) {

    }
}

@Composable
fun AppBluetoothDeviceServiceCharacteristicList(
    data: List<BluetoothDeviceServiceCharacteristic>,
    modifier: Modifier = Modifier,
    onClick: (id: String) -> Unit
) {
    data.forEach {
        AppBluetoothDeviceServiceCharacteristicListItem(data = it) { id ->
        }
    }
}

@Composable
fun AppBluetoothDeviceServiceCharacteristicListItem(
    bluetoothDeviceServiceCharacteristicDescriptorViewModel: BluetoothDeviceServiceCharacteristicDescriptorViewModel = viewModel(factory = AppViewModelProvider.Factory),
    data: BluetoothDeviceServiceCharacteristic,
    onClick: (id: String) -> Unit
) {
    val descriptors =
        bluetoothDeviceServiceCharacteristicDescriptorViewModel.getAllItemsByCharacteristicId(data.id).collectAsState(listOf())

    ListItem(
        overlineContent = { Text("Characteristic") },
        headlineContent = { Text(data.id) },
        supportingContent = {
            Column {
                Text("permissions:")
                Text(CharacteristicDescriptorPermission.getAll(data.permissions).toString())
                Text("properties:")
                Text(CharacteristicProperty.getAll(data.properties).toString())
                Text("writeTypes:")
                Text(CharacteristicWriteType.getAll(data.writeType).toString())
            }
        },
    )
    AppBluetoothDeviceServiceCharacteristicDescriptorList(
        data = descriptors.value,
        characteristic = data
    ) {

    }
}

@Composable
fun AppBluetoothDeviceServiceCharacteristicDescriptorList(
    characteristic: BluetoothDeviceServiceCharacteristic,
    data: List<BluetoothDeviceServiceCharacteristicDescriptor>,
    modifier: Modifier = Modifier,
    onClick: (id: String) -> Unit
) {
    data.forEach {
        AppBluetoothDeviceServiceCharacteristicDescriptorListItem(
            data = it,
            characteristic = characteristic
        ) { id ->
        }
    }
}

@Composable
fun AppBluetoothDeviceServiceCharacteristicDescriptorListItem(
    characteristic: BluetoothDeviceServiceCharacteristic,
    data: BluetoothDeviceServiceCharacteristicDescriptor,
    appBluetoothConnectViewModel: AppBluetoothConnectViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onClick: (id: String) -> Unit,
) {

    ListItem(
        overlineContent = { Text("Descriptor") },
        headlineContent = { Text(data.id) },
        supportingContent = {
            Column {
                if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                appBluetoothConnectViewModel.enableNotificationsAndIndications(
                                    serviceId = characteristic.serviceId,
                                    characteristicId = characteristic.id,
                                    descriptorId = data.id,
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
                                appBluetoothConnectViewModel.enableNotificationsAndIndications(
                                    serviceId = characteristic.serviceId,
                                    characteristicId = characteristic.id,
                                    descriptorId = data.id,
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

