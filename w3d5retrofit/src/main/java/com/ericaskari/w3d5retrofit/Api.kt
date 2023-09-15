package com.ericaskari.w3d5retrofit

import com.ericaskari.w3d5retrofit.entities.Actor
import com.ericaskari.w3d5retrofit.entities.Movie
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

public interface Api {
    @Headers(
        "Accept: application/json"
    )
    @GET("movies.json")
    abstract fun getMovies(): Call<List<Movie>>
    @Headers(
        "Accept: application/json"
    )
    @GET("actors.json")
    abstract fun getActors(): Call<List<Actor>>
}