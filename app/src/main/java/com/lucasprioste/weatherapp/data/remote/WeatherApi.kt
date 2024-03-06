package com.lucasprioste.weatherapp.data.remote

import com.lucasprioste.weatherapp.data.remote.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast")
    suspend fun getForecast(
        @Query("q") city: String?=null,
        @Query("lat") lat: Double?=null,
        @Query("lon") lon: Double?=null,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("cnt") cnt: Int?
    ): WeatherResponseDto
}