package com.lucasprioste.weatherapp.data.repository

import com.lucasprioste.weatherapp.data.local.db.WeatherDataBase
import com.lucasprioste.weatherapp.data.local.preferences.Preferences
import com.lucasprioste.weatherapp.data.mapper.toLocalCityWeatherEntity
import com.lucasprioste.weatherapp.data.mapper.toWeatherInfoPerLocation
import com.lucasprioste.weatherapp.data.remote.WeatherApi
import com.lucasprioste.weatherapp.domain.models.WeatherInfoPerLocation
import com.lucasprioste.weatherapp.domain.repository.WeatherRepository
import com.lucasprioste.weatherapp.utils.Resource
import com.lucasprioste.weatherapp.utils.Status
import com.lucasprioste.weatherapp.utils.TypeRequestWeather
import com.lucasprioste.weatherapp.utils.UnitsRequest
import com.lucasprioste.weatherapp.utils.initLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Locale
import javax.inject.Inject

class WeatherRepositoryImp @Inject constructor(
    private val api: WeatherApi,
    db: WeatherDataBase,
    private val preferences: Preferences
): WeatherRepository {
    private val dao = db.dao

    override suspend fun getAllLocation(): Flow<Resource<List<WeatherInfoPerLocation>>> {
        return flow {
            val listLocation = mutableListOf<WeatherInfoPerLocation>()
            emit(Resource.loading(null))
            val storageData = dao.getAllLocation()

            val isToPopulateDB = preferences.getPopulateDb() && storageData.isEmpty()
            if (isToPopulateDB){
                preferences.changePopulateDb(false)
                initLocation.map {
                    try {
                        getWeather(
                            typeRequestWeather = TypeRequestWeather.ByLatLon(lat = it.lat, lon = it.lon),
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
            }else{ //DB IS POPULATED
                storageData.map {
                    try {
                        getWeather(
                            typeRequestWeather = TypeRequestWeather.ByLatLon(lat = it.lat, lon = it.lon),
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
                        api.getForecast(
                            city = typeRequestWeather.name,
                            units = units.units,
                            lang = langId,
                            cnt = if (justWeatherConditionForTheExactTime) 1 else null
                        )
                    }
                    is TypeRequestWeather.ByLatLon -> {
                        api.getForecast(
                            lat = typeRequestWeather.lat,
                            lon = typeRequestWeather.lon,
                            units = units.units,
                            lang = langId,
                            cnt = if (justWeatherConditionForTheExactTime) 1 else null
                        )
                    }
                }
                val data = response.toWeatherInfoPerLocation()

                val existsCity = dao.getLocation(
                    idCity = data.city.idCity,
                    lat = data.city.coord.lat,
                    lon = data.city.coord.lon
                ).firstOrNull()

                if (existsCity == null){ //IF LOCATION DONT EXISTS IN DB ADD
                    dao.insertLocalCityWeatherEntity(data.toLocalCityWeatherEntity())
                }
                emit(Resource.success(data = data))
            }catch (e: Exception){
                emit(Resource.error(e.message))
            }
        }
    }

    override suspend fun deleteLocation(location: WeatherInfoPerLocation): Boolean {
        return try {
            dao.deleteLocalCityWeatherEntity(location.toLocalCityWeatherEntity())
            true
        }catch (e: Exception){
            false
        }
    }
}
