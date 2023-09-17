package com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic

import android.bluetooth.BluetoothGattCharacteristic
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BluetoothDeviceServiceCharacteristic(
    @PrimaryKey val id: String,
    @ColumnInfo val serviceId: String,
    @ColumnInfo val deviceId: String,
    @ColumnInfo val permissions: Int,
    @ColumnInfo val properties: Int,
    @ColumnInfo val writeType: Int,
) {

    companion object {
        fun fromBluetoothGattCharacteristic(
            item: BluetoothGattCharacteristic,
            serviceId: String,
            deviceId: String
        ): BluetoothDeviceServiceCharacteristic {
            return BluetoothDeviceServiceCharacteristic(
                id = item.uuid.toString(),
                deviceId = deviceId,
                serviceId = serviceId,
                permissions = item.permissions,
                properties = item.properties,
                writeType = item.writeType
            )

        }

    }
}

