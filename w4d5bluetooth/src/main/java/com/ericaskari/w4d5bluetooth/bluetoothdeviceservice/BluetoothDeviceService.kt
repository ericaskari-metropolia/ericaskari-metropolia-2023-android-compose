package com.ericaskari.w4d5bluetooth.bluetoothdeviceservice

import android.bluetooth.BluetoothGattService
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ericaskari.w4d5bluetooth.bluetooth.models.toGss
import com.ericaskari.w4d5bluetooth.nordicsemiconductordatabase.BluetoothServiceInfo

@Entity
data class BluetoothDeviceService(
    @PrimaryKey val id: String,
    @ColumnInfo val gssId: String,
    @ColumnInfo val deviceAddress: String,
    @ColumnInfo val viewName: String?,
    @ColumnInfo val viewIdentifier: String?,
    @ColumnInfo val viewSource: String?,
    @ColumnInfo val viewUuid: String?,

    ) {

    companion object {
        fun fromBluetoothGattService(
            item: BluetoothGattService,
            deviceAddress: String,
            bluetoothServiceInfoList: List<BluetoothServiceInfo>
        ): BluetoothDeviceService {
            val gssId = item.uuid.toGss()

            val nameInfo = bluetoothServiceInfoList.find { it.source == "gss" && it.uuid == gssId }

            return BluetoothDeviceService(
                id = item.uuid.toString(),
                deviceAddress = deviceAddress,
                gssId = item.uuid.toGss(),
                viewName = nameInfo?.name,
                viewIdentifier = nameInfo?.identifier,
                viewSource = nameInfo?.source,
                viewUuid = nameInfo?.uuid,
            )
        }
    }
}

