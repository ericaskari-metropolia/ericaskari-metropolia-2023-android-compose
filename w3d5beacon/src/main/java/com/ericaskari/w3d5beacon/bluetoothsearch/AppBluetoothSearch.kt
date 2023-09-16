package com.ericaskari.w3d5beacon.bluetoothsearch

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppBluetoothSearch(
    @PrimaryKey val address: String,
    @ColumnInfo val deviceName: String?,
    @ColumnInfo val rssi: Int,
    @ColumnInfo val manufacturer: String?,
    @ColumnInfo val lastSeen: Long?,
) {

    companion object {
        @SuppressLint("MissingPermission")
        fun fromScanResult(searchResult: ScanResult): AppBluetoothSearch {
            return AppBluetoothSearch(
                address = searchResult.device.address,
                deviceName = searchResult.device.name,
                rssi = searchResult.rssi,
                manufacturer = null,
                lastSeen = searchResult.timestampNanos
            )


        }

    }
}

