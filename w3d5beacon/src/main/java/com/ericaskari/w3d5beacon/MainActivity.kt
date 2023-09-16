package com.ericaskari.w3d5beacon

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ericaskari.w3d5beacon.application.data.AppViewModelProvider
import com.ericaskari.w3d5beacon.bluetooth.AppBluetoothManager
import com.ericaskari.w3d5beacon.bluetooth.AppBluetoothObserver
import com.ericaskari.w3d5beacon.bluetooth.AppMinimalBluetoothViewModel
import com.ericaskari.w3d5beacon.ui.theme.FirstComposeAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MainActivity : ComponentActivity() {
    private val bluetoothManager: Lazy<BluetoothManager> = lazy { getSystemService(BluetoothManager::class.java) }
    private val bluetoothAdapter: Lazy<BluetoothAdapter?> = lazy { bluetoothManager.value.adapter }
    lateinit var btEnableResultLauncher: ActivityResultLauncher<Intent>

    companion object {
        private val parentJob = Job()
        private val coroutineScope = CoroutineScope(Dispatchers.Default + parentJob)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appBluetoothManager = bluetoothAdapter.value?.let {
            AppBluetoothManager(
                bluetoothAdapter = it,
                scope = coroutineScope
            )
        }
        println("[MainActivity] appBluetoothManager is good =  ${appBluetoothManager != null}")
        val appBluetoothObserver = appBluetoothManager?.let {
            AppBluetoothObserver(
                activity = this,
                appBluetoothManager = it,
                bluetoothAdapter = bluetoothAdapter.value!!
            )
        }
        println("[MainActivity] appBluetoothManager is good =  ${appBluetoothObserver != null}")

        if (appBluetoothObserver != null) {
            this.lifecycle.addObserver(appBluetoothObserver)
        }


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
fun ApplicationContent(wikiViewModel: AppMinimalBluetoothViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val isScanning = wikiViewModel.isScanning.collectAsState()
    val scannerMessage = wikiViewModel.scannerMessage.collectAsState()
    val bleMessage = wikiViewModel._bleMessage.collectAsState()
//    val services = wikiViewModel._services.collectAsState()

    Column {
        Text(text = "isScanning: ${isScanning.value}")
        Text(text = "scannerMessage: ${scannerMessage.value}")
        Text(text = "scannerMessage: ${scannerMessage.value}")
        Text(text = "bleMessage: ${bleMessage.value}")
//        Text(text = "services: ${services.value}")
//        Text(text = "getReadBytes: ${wikiViewModel.getReadBytes()}")
//        Text(text = "getOnOffState: ${wikiViewModel.getOnOffState()}")
        Greeting("Android")
        Button(onClick = { wikiViewModel.startScan() }) {
            Text(text = "Start Scan")
        }
        Button(onClick = { wikiViewModel.stopScan() }) {
            Text(text = "Stop Scan")
        }
    }
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