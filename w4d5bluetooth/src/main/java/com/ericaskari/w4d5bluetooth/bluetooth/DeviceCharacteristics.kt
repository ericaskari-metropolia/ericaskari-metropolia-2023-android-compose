package com.ericaskari.w4d5bluetooth.bluetooth

import com.ericaskari.w4d5bluetooth.bluetooth.models.CharacteristicProperty
import com.ericaskari.w4d5bluetooth.bluetooth.models.CharacteristicWriteType

data class DeviceCharacteristics(
    val uuid: String,
    val name: String,
    val descriptor: String?,
    val permissions: Int,
    val properties: List<CharacteristicProperty>,
    val writeTypes: List<CharacteristicWriteType>,
    val descriptors: List<DeviceDescriptor>,
    val canRead: Boolean,
    val canWrite: Boolean,
    val readBytes: ByteArray?,
    val notificationBytes: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeviceCharacteristics

        if (uuid != other.uuid) return false
        if (name != other.name) return false
        if (descriptor != other.descriptor) return false
        if (permissions != other.permissions) return false
        if (properties != other.properties) return false
        if (writeTypes != other.writeTypes) return false
        if (descriptors != other.descriptors) return false
        if (canRead != other.canRead) return false
        if (canWrite != other.canWrite) return false
        if (readBytes != null) {
            if (other.readBytes == null) return false
            if (!readBytes.contentEquals(other.readBytes)) return false
        } else if (other.readBytes != null) return false
        if (notificationBytes != null) {
            if (other.notificationBytes == null) return false
            if (!notificationBytes.contentEquals(other.notificationBytes)) return false
        } else if (other.notificationBytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (descriptor?.hashCode() ?: 0)
        result = 31 * result + permissions
        result = 31 * result + properties.hashCode()
        result = 31 * result + writeTypes.hashCode()
        result = 31 * result + descriptors.hashCode()
        result = 31 * result + canRead.hashCode()
        result = 31 * result + canWrite.hashCode()
        result = 31 * result + (readBytes?.contentHashCode() ?: 0)
        result = 31 * result + (notificationBytes?.contentHashCode() ?: 0)
        return result
    }
}