package com.ericaskari.w4d5bluetooth.bluetoothdeviceservice

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BluetoothDeviceService(
    @PrimaryKey val id: String,
    @ColumnInfo val deviceId: String,
) {

    companion object {


    }
}

