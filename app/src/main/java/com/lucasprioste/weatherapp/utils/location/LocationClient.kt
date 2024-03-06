package com.lucasprioste.weatherapp.utils.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    suspend fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String): Exception()
}