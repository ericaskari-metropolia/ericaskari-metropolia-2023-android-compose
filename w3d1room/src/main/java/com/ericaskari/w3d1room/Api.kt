package com.ericaskari.w3d1room

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

public interface Api {
    @Headers(
        "Accept: application/json"
    )
    @GET("users")
    abstract fun getUsers(): Call<List<UserModel>>
}