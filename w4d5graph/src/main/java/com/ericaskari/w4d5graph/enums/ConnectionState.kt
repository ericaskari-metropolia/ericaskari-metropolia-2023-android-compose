package com.ericaskari.w4d5graph.enums

import android.bluetooth.BluetoothProfile


enum class ConnectionState {
    CONNECTED,
    CONNECTING,
    DISCONNECTING,
    DISCONNECTED,
    UNKNOWN;

    fun isActive() = (this == CONNECTING || this == CONNECTED)

    fun toTitle() = this.name.lowercase().replaceFirstChar { it.uppercase() }

    companion object {
        fun fromBluetoothProfileState(state: Int): ConnectionState {
            return when (state) {
                BluetoothProfile.STATE_CONNECTING -> CONNECTING

                BluetoothProfile.STATE_CONNECTED -> CONNECTED

                BluetoothProfile.STATE_DISCONNECTING -> DISCONNECTING

                BluetoothProfile.STATE_DISCONNECTED -> DISCONNECTED

                else -> DISCONNECTED
            }
        }
    }
}

