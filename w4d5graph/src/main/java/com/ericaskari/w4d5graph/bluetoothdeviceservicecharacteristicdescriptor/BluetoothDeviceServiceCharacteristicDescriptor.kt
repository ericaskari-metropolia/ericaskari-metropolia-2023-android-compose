package com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristicdescriptor

import android.bluetooth.BluetoothGattDescriptor
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BluetoothDeviceServiceCharacteristicDescriptor(
    @PrimaryKey val id: String,
    @ColumnInfo val characteristicId: String,
) {

    companion object {
        fun fromBluetoothGattDescriptor(
            characteristicId: String,
            item: BluetoothGattDescriptor,
        ): BluetoothDeviceServiceCharacteristicDescriptor {
            return BluetoothDeviceServiceCharacteristicDescriptor(item.uuid.toString(), characteristicId)

        }

    }

}

