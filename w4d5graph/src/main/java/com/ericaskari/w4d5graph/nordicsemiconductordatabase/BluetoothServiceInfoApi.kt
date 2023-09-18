package com.ericaskari.w4d5graph.nordicsemiconductordatabase

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

public interface BluetoothServiceInfoApi {
    @Headers(
        "Accept: application/json"
    )
    @GET("NordicSemiconductor/bluetooth-numbers-database/master/v1/service_uuids.json")
    abstract fun getBluetoothServices(): Call<List<BluetoothServiceInfo>>

}