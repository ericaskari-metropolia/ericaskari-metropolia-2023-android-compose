package com.ericaskari.w4d5bluetooth

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ericaskari.w4d5bluetooth.application.MyApplication
import com.ericaskari.w4d5bluetooth.application.data.AppViewModelProvider
import com.ericaskari.w4d5bluetooth.bluetooth.AppBluetoothViewModel
import com.ericaskari.w4d5bluetooth.bluetooth.models.CharacteristicPermission
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
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "devices") {
                    composable("devices") { backStackEntry ->
                        DevicesPage(navController)
                    }
                    composable(
                        "devices/{deviceId}",
                        arguments = listOf(navArgument("deviceId") { type = NavType.StringType }),
                    ) { backStackEntry ->
                        DeviceDetailsPage(navController, backStackEntry.arguments?.getString("deviceId"))
                    }
                }
            }
        }
    }
}

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
                        requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
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

@Composable
fun DeviceDetailsPage(
    navHostController: NavHostController,
    deviceId: String?,
    bluetoothDeviceViewModel: BluetoothDeviceViewModel = viewModel(factory = AppViewModelProvider.Factory),
    bluetoothDeviceServiceViewModel: BluetoothDeviceServiceViewModel = viewModel(factory = AppViewModelProvider.Factory),
    appBluetoothConnectViewModel: AppBluetoothConnectViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val connectMessage = appBluetoothConnectViewModel.connectMessage.collectAsState("unknown")

    Column {
        Button(onClick = { appBluetoothConnectViewModel.connect(deviceId!!) }) {
            Text(text = "Connect")
        }
        Button(onClick = { appBluetoothConnectViewModel.close() }) {
            Text(text = "Close")
        }
        Row {
            Text(text = "Status: ")
            Text(text = connectMessage.value.toString())
        }
        val item = bluetoothDeviceViewModel.getItemStream(deviceId!!).collectAsState(initial = null)
        item.value?.let {
            val services = bluetoothDeviceServiceViewModel.getAllItemsByDeviceId(it.address).collectAsState(listOf())
            AppBluetoothDeviceServiceList(
                serviceList = services.value
            ) {

            }
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
            AppBluetoothDeviceListItem(device = it) { id ->
                onClick(id)
            }
        }
    }

}


@Composable
fun AppBluetoothDeviceListItem(
    device: BluetoothDevice,
    onClick: (id: String) -> Unit
) {

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
        trailingContent = { device.lastSeen?.let { Text(SimpleDateFormat("MM/dd/yy h:mm:ss ", Locale.US).format(Date(device.lastSeen))) } }
    )

}

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
        items(serviceList) {
            AppBluetoothDeviceServiceListItem(service = it) { id ->
                onClick(id)
            }
        }
    }
}


@Composable
fun AppBluetoothDeviceServiceListItem(
    bluetoothDeviceServiceCharacteristicViewModel: BluetoothDeviceServiceCharacteristicViewModel = viewModel(factory = AppViewModelProvider.Factory),
    service: BluetoothDeviceService,
    onClick: (id: String) -> Unit
) {
    val characteristic = bluetoothDeviceServiceCharacteristicViewModel.getAllItemsByServiceId(service.id).collectAsState(listOf())

    ListItem(
        overlineContent = { Text("Service") },
        headlineContent = { Text(service.id) },
    )
    AppBluetoothDeviceServiceCharacteristicList(
        CharacteristicList = characteristic.value
    ) {

    }
}

@Composable
fun AppBluetoothDeviceServiceCharacteristicList(
    CharacteristicList: List<BluetoothDeviceServiceCharacteristic>,
    modifier: Modifier = Modifier,
    onClick: (id: String) -> Unit
) {
    CharacteristicList.forEach {
        AppBluetoothDeviceServiceCharacteristicListItem(characteristic = it) { id ->
        }
    }
}

@Composable
fun AppBluetoothDeviceServiceCharacteristicListItem(
    bluetoothDeviceServiceCharacteristicDescriptorViewModel: BluetoothDeviceServiceCharacteristicDescriptorViewModel = viewModel(factory = AppViewModelProvider.Factory),
    appBluetoothConnectViewModel: AppBluetoothConnectViewModel = viewModel(factory = AppViewModelProvider.Factory),
    characteristic: BluetoothDeviceServiceCharacteristic,
    onClick: (id: String) -> Unit
) {
    val descriptors =
        bluetoothDeviceServiceCharacteristicDescriptorViewModel.getAllItemsByCharacteristicId(characteristic.id).collectAsState(listOf())

    ListItem(
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
                                appBluetoothConnectViewModel.writeCharacteristic(
                                    serviceId = characteristic.serviceId,
                                    characteristicId = characteristic.id,
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
                                appBluetoothConnectViewModel.writeCharacteristic(
                                    serviceId = characteristic.serviceId,
                                    characteristicId = characteristic.id,
                                    value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                )
                            }
                        },
                    ) {
                        Text(text = "ENABLE_NOTIFICATION_VALUE")
                    }
                }


                Text("descriptors:")
                AppBluetoothDeviceServiceCharacteristicDescriptorList(
                    data = descriptors.value,
                    characteristic = characteristic
                ) {

                }
            }
        },
    )

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
            descriptor = it,
            characteristic = characteristic
        ) { id ->
        }
    }
}

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

