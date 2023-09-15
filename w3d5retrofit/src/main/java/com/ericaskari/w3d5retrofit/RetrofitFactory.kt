package com.ericaskari.w3d5retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {

    fun makeRetrofitService(): Api {
        return  Retrofit.Builder()
            .baseUrl("https://users.metropolia.fi/~mohamas/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(Api::class.java)

    }
}