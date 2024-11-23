package com.igoryan94.filmsearch.data.entity

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

object API {
    interface TmdbApi {
        @GET("3/movie/popular")
        fun getFilms(
            @Query("api_key") apiKey: String,
            @Query("language") language: String,
            @Query("page") page: Int
        ): Call<TmdbResultsDto>
    }
}