package com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor

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
            item: BluetoothGattDescriptor,
            characteristicId: String
        ): BluetoothDeviceServiceCharacteristicDescriptor {
            return BluetoothDeviceServiceCharacteristicDescriptor(item.uuid.toString(), characteristicId)

        }

    }

}

