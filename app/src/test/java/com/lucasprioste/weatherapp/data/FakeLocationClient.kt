package com.lucasprioste.weatherapp.data

import android.location.Location
import com.lucasprioste.weatherapp.utils.location.LocationClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocationClient: LocationClient {
    val mockLat = 55.0
    val mockLon = 55.0
    override suspend fun getLocationUpdates(interval: Long): Flow<Location> {
        return flow {
            val realLocation = Location("provider")
            realLocation.latitude = mockLat
            realLocation.longitude = mockLon
            emit(realLocation)
        }
    }
}