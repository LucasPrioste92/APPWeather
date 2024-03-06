package com.lucasprioste.weatherapp.utils

sealed class TypeRequestWeather{
    data class ByCityName(val name: String): TypeRequestWeather()
    data class ByLatLon(val lat: Double, val lon: Double): TypeRequestWeather()
}
