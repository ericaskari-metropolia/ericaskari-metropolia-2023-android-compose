package com.ericaskari.w4d5bluetooth.nordicsemiconductordatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BluetoothServiceInfo(
    @ColumnInfo val name: String,
    @PrimaryKey val identifier: String,
    @ColumnInfo val uuid: String,
    @ColumnInfo val source: String
)