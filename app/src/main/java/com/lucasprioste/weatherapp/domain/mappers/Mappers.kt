package com.lucasprioste.weatherapp.domain.mappers

import com.lucasprioste.weatherapp.R
import com.lucasprioste.weatherapp.domain.models.AdditionalInfo
import com.lucasprioste.weatherapp.domain.models.WeatherInfoPerLocation
import com.lucasprioste.weatherapp.utils.getTempFormatted

fun WeatherInfoPerLocation.toAdditionalInfoList(): List<AdditionalInfo> {
    val actual = info.first()
    return listOf(
        AdditionalInfo(iconId = R.drawable.ic_sun, description = R.string.max, value = getTempFormatted(actual.main.tempMax.toInt())),
        AdditionalInfo(iconId = R.drawable.ic_thermostat, description = R.string.feels_like, value = getTempFormatted(actual.main.feelsLike.toInt())),
        AdditionalInfo(iconId = R.drawable.ic_umbrella, description = R.string.rainfall, value = "${(actual.precipitationProbability*100).toInt()}%"),
        AdditionalInfo(iconId = R.drawable.ic_humidity, description = R.string.humidity, value = "${actual.main.humidity}%"),
        AdditionalInfo(iconId = R.drawable.ic_wind_power, description = R.string.wind, value = actual.wind.getWindKmh()),
        AdditionalInfo(iconId = R.drawable.ic_visibility, description = R.string.visibility, value = actual.getVisibilityKm()),
    )
}