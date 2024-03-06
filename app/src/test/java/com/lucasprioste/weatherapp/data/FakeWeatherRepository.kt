package com.lucasprioste.weatherapp.data

import com.lucasprioste.weatherapp.data.local.db.entities.LocalCityWeatherEntity
import com.lucasprioste.weatherapp.data.mapper.toLocalCityWeatherEntity
import com.lucasprioste.weatherapp.domain.models.City
import com.lucasprioste.weatherapp.domain.models.Coord
import com.lucasprioste.weatherapp.domain.models.InfoPerTime
import com.lucasprioste.weatherapp.domain.models.WeatherInfoPerLocation
import com.lucasprioste.weatherapp.domain.repository.WeatherRepository
import com.lucasprioste.weatherapp.utils.Resource
import com.lucasprioste.weatherapp.utils.Status
import com.lucasprioste.weatherapp.utils.TypeRequestWeather
import com.lucasprioste.weatherapp.utils.UnitsRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.util.Locale
import kotlin.random.Random

class FakeWeatherRepository: WeatherRepository {

    val mockDataDBLocation = (0..50).map {
        LocalCityWeatherEntity(
            idCity = it,
            lat = it+1*0.5,
            lon = it+3*0.5,
        )
    }.toMutableList()

    val returnedData = mutableListOf<WeatherInfoPerLocation>()

    override suspend fun getAllLocation(): Flow<Resource<List<WeatherInfoPerLocation>>> {
        return flow {
            val listLocation = mutableListOf<WeatherInfoPerLocation>()
            emit(Resource.loading(null))
            mockDataDBLocation.map { city ->
                try {
                    getWeather(
                        typeRequestWeather = TypeRequestWeather.ByLatLon(
                            lat = city.lat,
                            lon = city.lon
                        ),
                        units = UnitsRequest.Metric,
                        langId = Locale.getDefault().displayLanguage,
                        justWeatherConditionForTheExactTime = true
                    ).collect{ response ->
                        when(response.status){
                            Status.LOADING -> Unit
                            Status.ERROR -> Unit
                            Status.SUCCESS -> {
                                response.data?.let { location ->
                                    listLocation.add(location)
                                }
                            }
                        }
                    }
                }catch (e: Exception){
                    emit(Resource.error(e.message))
                }
            }
            emit(Resource.success(listLocation))
        }
    }

    override suspend fun getWeather(
        typeRequestWeather: TypeRequestWeather,
        units: UnitsRequest,
        langId: String,
        justWeatherConditionForTheExactTime: Boolean
    ): Flow<Resource<WeatherInfoPerLocation>> {
        return flow {
            emit(Resource.loading(null))
            try {
                val response = when(typeRequestWeather){
                    is TypeRequestWeather.ByCityName -> {
                        val existCity = returnedData.find {
                            it.city.name == typeRequestWeather.name
                        }

                        WeatherInfoPerLocation(
                            city = City(
                                coord = Coord(
                                    lat = existCity?.city?.coord?.lat ?: Random.nextDouble(0.3257, 80.4557),
                                    lon = existCity?.city?.coord?.lon ?:Random.nextDouble(0.3257, 80.4557)
                                ),
                                idCity = existCity?.city?.idCity ?:Random.nextInt(),
                                sunset = LocalDateTime.now(),
                                sunrise = LocalDateTime.now(),
                                name = existCity?.city?.name ?: typeRequestWeather.name
                            ),
                            info = (0..if (justWeatherConditionForTheExactTime) 0 else 25).map {
                                InfoPerTime()
                            }
                        )
                    }
                    is TypeRequestWeather.ByLatLon -> {
                        val locationDB = mockDataDBLocation.find {
                            it.lat == typeRequestWeather.lat && it.lon == typeRequestWeather.lon
                        }
                        WeatherInfoPerLocation(
                            city = City(
                                coord = Coord(
                                    lat = locationDB?.lat ?: Random.nextDouble(0.3257, 80.4557),
                                    lon = locationDB?.lon ?: Random.nextDouble(0.3257, 80.4557)
                                ),
                                idCity = locationDB?.idCity ?: Random.nextInt(),
                                sunset = LocalDateTime.now(),
                                sunrise = LocalDateTime.now()
                            ),
                            info = (0..if (justWeatherConditionForTheExactTime) 0 else 25).map {
                                InfoPerTime()
                            }
                        )
                    }
                }

                val existsCity = mockDataDBLocation.find {
                    (it.lat == response.city.coord.lat && it.lon == response.city.coord.lon) ||
                    it.idCity == response.city.idCity
                }

                if (existsCity == null){ //IF LOCATION DONT EXISTS IN DB ADD
                    mockDataDBLocation.add(response.toLocalCityWeatherEntity())
                }
                returnedData.add(response)
                emit(Resource.success(data = response))
            }catch (e: Exception){
                emit(Resource.error(e.message))
            }
        }
    }

    override suspend fun deleteLocation(location: WeatherInfoPerLocation): Boolean {
        location.toLocalCityWeatherEntity()
        returnedData.removeIf { it.city.idCity == location.toLocalCityWeatherEntity().idCity }
        return mockDataDBLocation.removeIf { it.idCity == location.toLocalCityWeatherEntity().idCity }
    }
}