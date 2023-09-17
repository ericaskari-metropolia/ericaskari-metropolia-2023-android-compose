package com.ericaskari.w4d5bluetooth.bluetoothdeviceservice

import android.bluetooth.BluetoothGattService
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BluetoothDeviceService(
    @PrimaryKey val id: String,
    @ColumnInfo val deviceAddress: String,

    ) {

    companion object {
        fun fromBluetoothGattService(item: BluetoothGattService, deviceAddress: String): BluetoothDeviceService {
            return BluetoothDeviceService(item.uuid.toString(), deviceAddress)

        }

    }
}

