package com.ericaskari.w3d1room.api

import WikipediaResponseQueryBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

public interface WikiApi {
    @Headers(
        "Accept: application/json"
    )
    @GET("users")
    abstract fun getHits(
        @Query("srsearch") srsearch: String,
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("list") list: String = "search",
    ): Call<WikipediaResponseQueryBody>
}