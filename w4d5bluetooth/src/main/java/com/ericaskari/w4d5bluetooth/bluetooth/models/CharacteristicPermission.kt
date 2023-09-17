package com.ericaskari.w4d5bluetooth.bluetooth.models

enum class CharacteristicPermission(val value: Int) {
    PERMISSION_READ(1),
    PERMISSION_READ_ENCRYPTED(2),
    PERMISSION_READ_ENCRYPTED_MITM(4),
    PERMISSION_WRITE(16),
    PERMISSION_WRITE_ENCRYPTED(32),
    PERMISSION_WRITE_ENCRYPTED_MITM(64),
    PERMISSION_WRITE_SIGNED(128),
    PERMISSION_WRITE_SIGNED_MITM(256);

    companion object {
        fun getAll(bleValue: Int): List<CharacteristicPermission> {
            // Binary AND
            return values().filter { (bleValue and it.value) > 0 }
        }
    }
}

fun List<CharacteristicPermission>.canWritePermissions(): Boolean = this.any(
    listOf(
        CharacteristicPermission.PERMISSION_WRITE,
        CharacteristicPermission.PERMISSION_WRITE_ENCRYPTED,
        CharacteristicPermission.PERMISSION_WRITE_ENCRYPTED_MITM,
        CharacteristicPermission.PERMISSION_WRITE_SIGNED,
        CharacteristicPermission.PERMISSION_WRITE_SIGNED_MITM
    )::contains
)
