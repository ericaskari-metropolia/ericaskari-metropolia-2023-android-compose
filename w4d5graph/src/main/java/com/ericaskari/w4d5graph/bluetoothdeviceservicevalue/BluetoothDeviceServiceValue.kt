package com.ericaskari.w4d5graph.bluetoothdeviceservicevalue

import android.bluetooth.BluetoothGattCharacteristic
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ericaskari.w4d5graph.bluetooth.models.toHex
import com.ericaskari.w4d5graph.bluetooth.models.toInteger
import java.util.Date

@Entity
data class BluetoothDeviceServiceValue(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val createdAt: Date,
    @ColumnInfo val deviceId: String,
    @ColumnInfo val serviceId: String,
    @ColumnInfo val characteristicId: String,
    @ColumnInfo val descriptorId: String?,
    @ColumnInfo val hexValue: String,
    @ColumnInfo val integerValue: Int,

    ) {

    companion object {
        fun fromCharacteristic(
            deviceId: String,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
        ): BluetoothDeviceServiceValue {
            return BluetoothDeviceServiceValue(
                id = 0,
                createdAt = Date(),
                deviceId = deviceId,
                serviceId = characteristic.service.uuid.toString(),
                characteristicId = characteristic.uuid.toString(),
                descriptorId = null,
                hexValue = value.toHex(),
                integerValue = value.toInteger()
            )
        }
    }
}

