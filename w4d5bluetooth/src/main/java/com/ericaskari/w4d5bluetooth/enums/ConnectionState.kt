package com.ericaskari.w4d5bluetooth.enums


enum class ConnectionState {
    CONNECTED,
    CONNECTING,
    DISCONNECTING,
    DISCONNECTED,
    UNKNOWN;

    fun isActive() = (this == CONNECTING || this == CONNECTED)

    fun toTitle() = this.name.lowercase().replaceFirstChar { it.uppercase() }
}

