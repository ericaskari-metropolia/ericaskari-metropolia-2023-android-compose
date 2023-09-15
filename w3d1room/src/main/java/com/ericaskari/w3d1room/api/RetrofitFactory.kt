package com.ericaskari.w3d1room.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    fun makeMetropoliaApiService(): MetropoliaApi {
        return Retrofit.Builder()
            .baseUrl("https://users.metropolia.fi/~mohamas/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(MetropoliaApi::class.java)

    }

    fun makeWikiApiService(): WikiApi {
        return Retrofit.Builder()
            .baseUrl("https://en.wikipedia.org/w/api.php/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(WikiApi::class.java)

    }
}