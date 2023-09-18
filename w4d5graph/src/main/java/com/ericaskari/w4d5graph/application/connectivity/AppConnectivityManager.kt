package com.ericaskari.w4d5graph.application.connectivity

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build

class AppConnectivityManager(
    private val application: Application,
    val onChange: (isConnected: InternetConnectionState) -> Unit
) {
    init {
        register()
    }

    @SuppressLint("ObsoleteSdkInt")
    fun register() {
        (application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).let { connectivityManager ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                registerAPI24AndAbove(connectivityManager)
            } else {
                registerAPI24Below(connectivityManager)
            }
        }
    }


    private fun registerAPI24AndAbove(connectivityManager: ConnectivityManager) {
        connectivityManager.registerDefaultNetworkCallback(buildNetworkCallback())
    }


    @SuppressLint("WrongConstant")
    private fun registerAPI24Below(connectivityManager: ConnectivityManager) {
        NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addCapability(NetworkCapabilities.TRANSPORT_WIFI)
            .build().let { networkRequest ->
                connectivityManager.registerNetworkCallback(networkRequest, buildNetworkCallback())
            }
    }

    private fun buildNetworkCallback(): ConnectivityManager.NetworkCallback {
        return object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                this@AppConnectivityManager.onChange(InternetConnectionState.CONNECTED)
            }

            override fun onLost(network: Network) {
                this@AppConnectivityManager.onChange(InternetConnectionState.DISCONNECTED)
            }

        }
    }

}