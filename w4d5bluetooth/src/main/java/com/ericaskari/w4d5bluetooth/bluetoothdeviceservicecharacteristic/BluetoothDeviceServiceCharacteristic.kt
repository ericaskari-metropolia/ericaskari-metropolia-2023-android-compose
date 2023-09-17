package com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic

import android.bluetooth.BluetoothGattCharacteristic
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BluetoothDeviceServiceCharacteristic(
    @PrimaryKey val id: String,
    @ColumnInfo val serviceId: String,
) {

    companion object {
        fun fromBluetoothGattCharacteristic(item: BluetoothGattCharacteristic, serviceId: String): BluetoothDeviceServiceCharacteristic {
            return BluetoothDeviceServiceCharacteristic(item.uuid.toString(), serviceId)

        }

    }
}

