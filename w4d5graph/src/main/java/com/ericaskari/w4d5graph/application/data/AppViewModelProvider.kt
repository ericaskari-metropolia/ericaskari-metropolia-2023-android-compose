/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ericaskari.w4d5graph.application.data

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ericaskari.w4d5graph.application.MyApplication
import com.ericaskari.w4d5graph.bluetooth.AppBluetoothViewModel
import com.ericaskari.w4d5graph.bluetoothconnect.AppBluetoothConnectViewModel
import com.ericaskari.w4d5graph.bluetoothdeviceservice.BluetoothDeviceServiceViewModel
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristicViewModel
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptorViewModel
import com.ericaskari.w4d5graph.bluetoothdeviceservicevalue.BluetoothDeviceServiceValueViewModel
import com.ericaskari.w4d5graph.bluetoothsearch.BluetoothDeviceViewModel
import com.ericaskari.w4d5graph.nordicsemiconductordatabase.BluetoothServiceInfoViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AppBluetoothViewModel(
                appBluetoothManager = MyApplication().appBluetoothManager,
                appBluetoothObserver = MyApplication().appBluetoothObserver,
            )
        }
        addInitializer(AppBluetoothConnectViewModel::class) {
            AppBluetoothConnectViewModel(
                MyApplication().appBluetoothGattService
            )
        }
        addInitializer(BluetoothDeviceViewModel::class) {
            BluetoothDeviceViewModel(
                MyApplication().container.bluetoothDeviceRepository
            )
        }
        addInitializer(BluetoothDeviceServiceViewModel::class) {
            BluetoothDeviceServiceViewModel(
                MyApplication().container.bluetoothDeviceServiceRepository
            )
        }
        addInitializer(BluetoothDeviceServiceCharacteristicViewModel::class) {
            BluetoothDeviceServiceCharacteristicViewModel(
                MyApplication().container.bluetoothDeviceServiceCharacteristicRepository
            )
        }
        addInitializer(BluetoothDeviceServiceCharacteristicDescriptorViewModel::class) {
            BluetoothDeviceServiceCharacteristicDescriptorViewModel(
                MyApplication().container.bluetoothDeviceServiceCharacteristicDescriptorRepository
            )
        }
        addInitializer(BluetoothServiceInfoViewModel::class) {
            BluetoothServiceInfoViewModel(
                MyApplication().container.bluetoothServiceInfoRepository
            )
        }
        addInitializer(BluetoothDeviceServiceValueViewModel::class) {
            BluetoothDeviceServiceValueViewModel(
                MyApplication().container.bluetoothDeviceServiceValueRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of [MyApplication].
 */
fun CreationExtras.MyApplication(): MyApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
