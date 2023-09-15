package com.ericaskari.w3d5beacon.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AppBluetoothObserver(
    private val activity: ComponentActivity,
    private val appBluetoothManager: AppBluetoothManager,
    private val bluetoothAdapter: BluetoothAdapter
) : DefaultLifecycleObserver {
    private val registry: ActivityResultRegistry = activity.activityResultRegistry
    lateinit var btEnableResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var broadcastReceiver: BroadcastReceiver


    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        println("[BluetoothObserver] onCreate")
        createBroadcastReceiver()
        btEnableResultLauncher = registerHandler(owner, "EnableBLE")

    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        println("[BluetoothObserver] onPause called")

        try {
            activity.unregisterReceiver(broadcastReceiver)
        } catch (_: Exception) {

        } finally {
            appBluetoothManager.stopScan()
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        println("[BluetoothObserver] onResume called")

        ContextCompat.registerReceiver(
            activity, broadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED),
            ContextCompat.RECEIVER_EXPORTED
        )

        if (!bluetoothAdapter.isEnabled)
            launchEnableBtAdapter()

        appBluetoothManager.scan()
    }

    private fun createBroadcastReceiver() {
        println("[BluetoothObserver] createBroadcastReceiver")
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {

                // activity.lifecycleScope.launch {
                val action = intent.action
                println("[BluetoothObserver] btadapter changed $action")

                // It means the user has changed their bluetooth state.
                if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    println("[BluetoothObserver] btadapter state: ${bluetoothAdapter.state} / ${bluetoothAdapter.isEnabled}")
                    if (bluetoothAdapter.state == BluetoothAdapter.STATE_OFF) {
                        //bleManager.isScanning.value = false
                        //bleManager.stopScan()
                        launchEnableBtAdapter()
                    }
                    if (bluetoothAdapter.state == BluetoothAdapter.STATE_ON) {
                        println("[BluetoothObserver] btadapter back on...")
                        //delay(300L)
                        //bleManager.scan()
                    }
                    //}
                }
            }


        }
    }

    private fun launchEnableBtAdapter() {
        //if (bleManager.scanEnabled) {
        try {
            val btEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            btEnableResultLauncher.launch(btEnableIntent)
        } catch (e: Exception) {
            //Timber.tag("LAUNCH_BT_ENABLE").e(e)
        }
        //}
    }

    private fun registerHandler(owner: LifecycleOwner, key: String) = registry.register(
        key,
        owner,
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultOk: Boolean = result.resultCode == Activity.RESULT_OK

        println("[BluetoothObserver] registerHandler:")
        println("[BluetoothObserver] Activity.RESULT_OK: $resultOk")
        if (resultOk) {
            // There are no request codes
            val data: Intent? = result.data
            //bleManager.scan()
        }
    }

}