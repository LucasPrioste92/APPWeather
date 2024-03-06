package com.lucasprioste.weatherapp.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.*
import com.lucasprioste.weatherapp.utils.hasLocationPermission
import com.lucasprioste.weatherapp.utils.location.LocationClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultLocationClient @Inject constructor(
    private val context: Context,
    private val client: FusedLocationProviderClient
): LocationClient {

    @SuppressLint("MissingPermission")
    override suspend fun getLocationUpdates(interval: Long) = callbackFlow {
        if (!context.hasLocationPermission()){
            throw LocationClient.LocationException("Missing Permission Location")
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isGpsEnabled && !isNetworkEnabled)
            throw LocationClient.LocationException("GPS is disabled")

        val request = LocationRequest.create()
            .setInterval(interval)
            .setFastestInterval(interval)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)

        val locationCallback = object : LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.locations.lastOrNull()?.let { location ->
                    launch { send(location) }
                }
            }
        }

        client.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose{
            client.removeLocationUpdates(locationCallback)
        }
    }

}