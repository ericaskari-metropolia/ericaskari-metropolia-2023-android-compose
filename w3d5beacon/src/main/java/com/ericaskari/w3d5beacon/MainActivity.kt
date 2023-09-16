package com.ericaskari.w3d5beacon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.ericaskari.w3d5beacon.bluetooth.AppBluetoothViewModel
import com.ericaskari.w3d5beacon.ui.theme.FirstComposeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
fun ApplicationContent(wikiViewModel: AppBluetoothViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val isScanning = wikiViewModel.isScanning.collectAsState()
    val scannerMessage = wikiViewModel.scannerMessage.collectAsState()
//    val services = wikiViewModel._services.collectAsState()

    Column {
        Text(text = "isScanning: ${isScanning.value}")
        Text(text = "scannerMessage: ${scannerMessage.value}")
        Text(text = "scannerMessage: ${scannerMessage.value}")
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