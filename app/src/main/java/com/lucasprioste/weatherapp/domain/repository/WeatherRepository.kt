package com.lucasprioste.weatherapp.domain.repository

import com.lucasprioste.weatherapp.domain.models.WeatherInfoPerLocation
import com.lucasprioste.weatherapp.utils.Resource
import com.lucasprioste.weatherapp.utils.TypeRequestWeather
import com.lucasprioste.weatherapp.utils.UnitsRequest
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getAllLocation(): Flow<Resource<List<WeatherInfoPerLocation>>>

    suspend fun getWeather(
        typeRequestWeather: TypeRequestWeather,
        units: UnitsRequest = UnitsRequest.Metric,
        langId: String,
        justWeatherConditionForTheExactTime: Boolean = false
    ): Flow<Resource<WeatherInfoPerLocation>>

    suspend fun deleteLocation(
        location: WeatherInfoPerLocation
    ): Boolean
}