package com.ericaskari.w4d5bluetooth.bluetooth.models

import android.bluetooth.BluetoothGattDescriptor

data class CharacteristicsInfo(
    val permissions: Int,
    val properties: List<CharacteristicProperty>,
    val writeTypes: List<CharacteristicWriteType>,
    val descriptors: Map<BluetoothGattDescriptor, List<CharacteristicDescriptorPermission>>
)
