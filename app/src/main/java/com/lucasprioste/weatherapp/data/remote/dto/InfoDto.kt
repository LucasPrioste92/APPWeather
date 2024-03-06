package com.lucasprioste.weatherapp.data.remote.dto

data class InfoDto (
    val cloudsDto: CloudsDto?=null,
    val dt: Int,
    val dt_txt: String,
    val main: MainDto,
    val pop: Double,
    val sys: SysDto,
    val visibility: Int,
    val weather: List<WeatherDto>,
    val wind: WindDto
)