package com.ericaskari.w3d5retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    const val BASE_URL = "https://jsonplaceholder.typicode.com"

    fun makeRetrofitService(): Api {
        return  Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(Api::class.java)

    }
}