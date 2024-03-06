package com.lucasprioste.weatherapp.data.mapper

import com.lucasprioste.weatherapp.data.local.db.entities.LocalCityWeatherEntity
import com.lucasprioste.weatherapp.domain.models.LocationSaved
import com.lucasprioste.weatherapp.domain.models.WeatherInfoPerLocation


fun WeatherInfoPerLocation.toLocalCityWeatherEntity(): LocalCityWeatherEntity {
    return LocalCityWeatherEntity(
        idCity = city.idCity,
        lat = city.coord.lat,
        lon = city.coord.lon
    )
}

fun LocalCityWeatherEntity.toLocationSaved(): LocationSaved {
    return LocationSaved(
        lat = lat,
        lon = lon
    )
}



