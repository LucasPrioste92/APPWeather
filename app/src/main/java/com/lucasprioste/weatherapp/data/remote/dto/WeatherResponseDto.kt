package com.lucasprioste.weatherapp.data.remote.dto

data class WeatherResponseDto(
    val city: CityDto,
    val cnt: Int,
    val cod: String,
    val list: List<InfoDto>,
    val message: Int
)