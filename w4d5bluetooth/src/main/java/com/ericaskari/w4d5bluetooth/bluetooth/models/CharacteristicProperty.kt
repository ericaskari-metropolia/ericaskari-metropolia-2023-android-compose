package com.ericaskari.w4d5bluetooth.bluetooth.models

enum class CharacteristicProperty(val value: Int) {
    PROPERTY_BROADCAST(1),              // 0000 0001
    PROPERTY_READ(2),                   // 0000 0010
    PROPERTY_WRITE_NO_RESPONSE(4),      // 0000 0100
    PROPERTY_WRITE(8),                  // 0000 1000
    PROPERTY_NOTIFY(16),                // 0001 0000
    PROPERTY_INDICATE(32),              // 0010 0000
    PROPERTY_SIGNED_WRITE(64),          // 0100 0000
    PROPERTY_EXTENDED_PROPS(128);       // 1000 0000

    companion object {

        fun getAll(bleValue: Int): List<CharacteristicProperty> {
            // Binary AND
            return values().filter { (bleValue and it.value) > 0 }
        }
    }
}

fun List<CharacteristicProperty>.canRead(): Boolean = this.contains(CharacteristicProperty.PROPERTY_READ)
fun List<CharacteristicProperty>.canWriteProperties(): Boolean = this.any(
    listOf(
        CharacteristicProperty.PROPERTY_WRITE,
        CharacteristicProperty.PROPERTY_SIGNED_WRITE,
        CharacteristicProperty.PROPERTY_WRITE_NO_RESPONSE
    )::contains
)
