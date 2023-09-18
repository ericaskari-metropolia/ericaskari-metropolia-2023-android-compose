package com.ericaskari.w4d5graph.application.connectivity


enum class InternetConnectionState {
    CONNECTED,
    DISCONNECTED,
    UNKNOWN;

    fun toTitle() = this.name.lowercase().replaceFirstChar { it.uppercase() }
    fun isConnected() = this.name == CONNECTED.name
}

