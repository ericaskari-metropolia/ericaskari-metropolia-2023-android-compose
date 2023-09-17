package com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BluetoothDeviceServiceCharacteristic(
    @PrimaryKey val id: String,
    @ColumnInfo val serviceId: String,
) {

    companion object {


    }
}

