package com.ericaskari.w4d5graph.bluetooth.models

enum class CharacteristicWriteType(val value: Int) {
    WRITE_TYPE_DEFAULT(2),
    WRITE_TYPE_NO_RESPONSE(1),
    WRITE_TYPE_SIGNED(4);

    companion object {
        fun getAll(bleValue: Int): List<CharacteristicWriteType> {
            // Binary AND
            return values().filter { (bleValue and it.value) > 0 }
        }
    }
}