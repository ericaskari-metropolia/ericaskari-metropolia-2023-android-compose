package com.ericaskari.w3d1room.api

import com.ericaskari.w3d1room.api.apimodels.President
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

public interface MetropoliaApi {
    @Headers(
        "Accept: application/json"
    )
    @GET("presidents.json")
    abstract fun getPresidents(): Call<List<President>>
}