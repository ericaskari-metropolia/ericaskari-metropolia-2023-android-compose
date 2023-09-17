package com.ericaskari.w4d5bluetooth.network

import com.ericaskari.w4d5bluetooth.nordicsemiconductordatabase.BluetoothServiceInfoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    fun makeBluetoothServiceInfoApi(): BluetoothServiceInfoApi {
        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(BluetoothServiceInfoApi::class.java)

    }
}