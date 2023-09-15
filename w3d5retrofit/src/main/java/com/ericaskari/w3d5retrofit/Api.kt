package com.ericaskari.w3d5retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

public interface Api {
    @Headers(
        "Accept: application/json"
    )
    @GET("users")
    abstract fun getUsers(): Call<List<UserModel>>
}