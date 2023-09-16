package com.ericaskari.w3d5beacon.bluetoothsearch

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppBluetoothSearch(
    @PrimaryKey val id: String,
    @ColumnInfo val name: String,
) {

}

