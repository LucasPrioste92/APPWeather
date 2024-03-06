package com.lucasprioste.weatherapp.domain.models

import com.lucasprioste.weatherapp.R
import com.lucasprioste.weatherapp.utils.WeatherStatusCondition

data class ConditionWeather(
    val description: String = "",
    val icon: Int = R.drawable.sun,
    val status: WeatherStatusCondition = WeatherStatusCondition.Clear,
){
    fun getDescriptionFormatted(): String{
        return description.replaceFirstChar { it.uppercase() }
    }
}