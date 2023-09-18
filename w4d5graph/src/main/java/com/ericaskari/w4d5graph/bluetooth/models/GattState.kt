package com.ericaskari.w4d5graph.bluetooth.models

enum class GattState(val value: Int) {
    GATT_CONNECTION_CONGESTED(143),
    GATT_FAILURE(257),
    GATT_INSUFFICIENT_AUTHENTICATION(5),
    GATT_INSUFFICIENT_AUTHORIZATION(8),
    GATT_INSUFFICIENT_ENCRYPTION(15),
    GATT_INVALID_ATTRIBUTE_LENGTH(13),
    GATT_INVALID_OFFSET(7),
    GATT_READ_NOT_PERMITTED(2),
    GATT_REQUEST_NOT_SUPPORTED(6),
    GATT_WRITE_NOT_PERMITTED(3),
    GATT_SUCCESS(0);

    companion object {

        fun fromState(state: Int): GattState {
            return values().find { it.value == state }!!
        }
    }
}
