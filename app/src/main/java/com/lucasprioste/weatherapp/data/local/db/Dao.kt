package com.lucasprioste.weatherapp.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lucasprioste.weatherapp.data.local.db.entities.LocalCityWeatherEntity

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalCityWeatherEntity(localCityWeatherEntity: LocalCityWeatherEntity): Long

    @Delete
    suspend fun deleteLocalCityWeatherEntity(localCityWeatherEntity: LocalCityWeatherEntity)

    @Query("SELECT * FROM localcityweatherentity")
    suspend fun getAllLocation(): List<LocalCityWeatherEntity>

    @Query("SELECT * FROM localcityweatherentity WHERE idCity=:idCity OR (lat=:lat AND lon=:lon)")
    suspend fun getLocation(idCity: Int, lat: Double, lon: Double): List<LocalCityWeatherEntity>
}