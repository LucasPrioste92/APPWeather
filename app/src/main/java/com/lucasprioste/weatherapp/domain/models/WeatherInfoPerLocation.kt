package com.lucasprioste.weatherapp.domain.models


data class WeatherInfoPerLocation(
    val city: City,
    val info: List<InfoPerTime>,
)
