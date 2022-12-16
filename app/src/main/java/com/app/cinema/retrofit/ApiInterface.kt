package com.app.cinema.retrofit

import com.app.cinema.model.MovieDescriptionResponse
import com.app.cinema.model.SearchMovieListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("/")
    suspend fun searchMovieList(
        @Query("s") s: String,
        @Query("apikey") apikey: String,
    ): Response<SearchMovieListResponse>

    @GET("/")
    suspend fun getMovieDescription(
        @Query("i") i: String,
        @Query("apikey") apikey: String,
    ): Response<MovieDescriptionResponse>

}