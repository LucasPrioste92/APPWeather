package com.lucasprioste.weatherapp.domain.models

import java.time.LocalDateTime

data class City(
    val coord: Coord,
    val country: String = "",
    val name: String = "",
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime,
    val timezone: Int = 0,
    val idCity: Int
){
    fun getCityNameFormatted(): String{
        val newName = name.replaceFirstChar { it.uppercase() }
        return newName.split(" ").first()
    }
}
