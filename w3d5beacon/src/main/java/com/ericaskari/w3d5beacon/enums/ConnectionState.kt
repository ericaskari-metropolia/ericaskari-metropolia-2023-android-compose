package com.ericaskari.w3d5beacon.enums


enum class ConnectionState {
    CONNECTED,
    CONNECTING,
    DISCONNECTING,
    DISCONNECTED,
    UNKNOWN;

    fun isActive() = (this == CONNECTING || this == CONNECTED)

    fun toTitle() = this.name.lowercase().replaceFirstChar { it.uppercase() }
}

