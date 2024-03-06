package com.lucasprioste.weatherapp.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocalCityWeatherEntity(
    @PrimaryKey val idCity: Int,
    val lat: Double,
    val lon: Double
)