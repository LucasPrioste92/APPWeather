package com.lucasprioste.weatherapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lucasprioste.weatherapp.data.local.db.entities.LocalCityWeatherEntity

@Database(
    entities = [LocalCityWeatherEntity::class],
    version = 1
)
abstract class WeatherDataBase: RoomDatabase() {
    abstract val dao: Dao
}