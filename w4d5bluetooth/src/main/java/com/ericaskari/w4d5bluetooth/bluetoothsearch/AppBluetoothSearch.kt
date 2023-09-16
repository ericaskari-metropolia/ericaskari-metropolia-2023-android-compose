package com.ericaskari.w4d5bluetooth.bluetoothsearch

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.os.SystemClock
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.concurrent.TimeUnit

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
                lastSeen = (System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(
                    SystemClock.elapsedRealtimeNanos() - searchResult.timestampNanos,
                    TimeUnit.NANOSECONDS
                ))
            )


        }

    }
}

