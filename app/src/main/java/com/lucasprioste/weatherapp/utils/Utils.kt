package com.lucasprioste.weatherapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.lucasprioste.weatherapp.domain.models.Coord
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

enum class WeatherStatusCondition{
    Thunderstorm, Drizzle, Rain, Snow, Atmosphere, Clear, Clouds
}

sealed class UnitsRequest(val units: String){
    object Metric: UnitsRequest(units = "metric") //Celsius
    object Imperial: UnitsRequest(units = "imperial") //Fahrenheit
}

fun getLocalDateTime(value: Int): LocalDateTime{
    val stamp = value.toLong()
    return LocalDateTime.ofInstant(Instant.ofEpochSecond(stamp), ZoneId.systemDefault())
}

const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
const val API_KEY = "d4f795032f1f70fd1679f62709446f84"

val initLocation = listOf(
    Coord(lat = 38.7167, lon = -9.1333), //Lisbon
    Coord(lat = 40.4168, lon = -3.7038), //Madrid
    Coord(lat = 48.8566, lon = 2.3522), //Paris
    Coord(lat = 52.5200, lon = 13.4050), //Berlin
    Coord(lat = 55.6761, lon = 12.5683), //Copenhagen
    Coord(lat = 41.9028, lon = 12.4964), //Rome
    Coord(lat = 51.5074, lon = -0.1278), //London
    Coord(lat = 53.3498, lon = -6.2603), //Dublin
    Coord(lat = 50.0755, lon = 14.4378), //Prague
    Coord(lat = 48.2082, lon = 16.3738) //Vienna
)

const val FILE_NAME_PREFERENCES = "INFO_WEATHER_APP"
const val POPULATE_DB_KEY = "POPULATE_DB"

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.hasLocationPermission(): Boolean{
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun getTempFormatted(value: Int): String{
    return "${value}º"
}