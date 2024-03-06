package com.lucasprioste.weatherapp.domain.models

import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*

data class InfoPerTime(
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val main: MainInfo = MainInfo(),
    val precipitationProbability: Double = 0.0, //0-1, where 1 = 100% and 0 = 0%
    val isDay: Boolean = false,
    val visibility: Int = 0,
    val weatherCondition: ConditionWeather = ConditionWeather(),
    val wind: Wind = Wind(),
    val id: Int?=null
){
    fun getDateTimeFormatted(): String{
        return "${dateTime.dayOfMonth} ${dateTime.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}, ${dateTime.dayOfWeek.getDisplayName(TextStyle.FULL,
            Locale.getDefault())}"
    }

    fun getDateDayAndMonth(): String{
        return "${dateTime.dayOfMonth}/${dateTime.monthValue}"
    }

    fun getWeatherInfoFormatted(): String{
        return "${main.temp.toInt()}º"
    }

    fun getTimeFormatted(): String{
        return "${dateTime.hour}h"
    }

    fun getVisibilityKm(): String{
        val value = (visibility*0.001)/1
        return "${value.toInt()} km"
    }
}
