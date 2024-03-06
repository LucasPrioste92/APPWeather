package com.lucasprioste.weatherapp.domain.models

data class MainInfo(
    val feelsLike: Double = 0.0,
    val atmosphericGroundLevel: Int = 0,
    val humidity: Int = 0,
    val pressure: Int = 0,
    val atmosphericSeaLevel: Int = 0,
    val temp: Double = 0.0,
    val tempMax: Double = 0.0,
    val tempMin: Double = 0.0
){
    fun tempFormatted(): String{
        return "${temp.toInt()}º"
    }
}
