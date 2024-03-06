package com.lucasprioste.weatherapp.domain.models

data class Wind(
    val deg: Int = 0,
    val gust: Double = 0.0,
    val speed: Double = 0.0
){
    fun getWindKmh(): String{
        val value = speed*3.6
        return "${value.toInt()} km/h"
    }
}
