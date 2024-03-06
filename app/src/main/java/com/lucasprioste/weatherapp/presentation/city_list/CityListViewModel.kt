package com.lucasprioste.weatherapp.presentation.city_list

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasprioste.weatherapp.R
import com.lucasprioste.weatherapp.domain.models.WeatherInfoPerLocation
import com.lucasprioste.weatherapp.domain.repository.SessionRepository
import com.lucasprioste.weatherapp.domain.repository.WeatherRepository
import com.lucasprioste.weatherapp.utils.Resource
import com.lucasprioste.weatherapp.utils.Status
import com.lucasprioste.weatherapp.utils.TypeRequestWeather
import com.lucasprioste.weatherapp.utils.UnitsRequest
import com.lucasprioste.weatherapp.utils.location.LocationClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CityListViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationService: LocationClient,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _cityList = MutableStateFlow<Resource<List<WeatherInfoPerLocation>>>(Resource.loading(null))
    val cityList = _cityList.asStateFlow()

    private val _error = MutableStateFlow<Int?>(null)
    val error = _error.asStateFlow()

    private val _action = MutableStateFlow<CityListContract.CityListAction?>(null)
    val action = _action.asStateFlow()

    private var fetchingLocationGps: Job? = null

    init {
        fetchCitiesWeather()
    }

    fun onEvent(event: CityListContract.CityListEvent){
        when(event){
            is CityListContract.CityListEvent.OnDeleteLocation -> {
                deleteLocation(event.item)
            }
            is CityListContract.CityListEvent.OnSearch -> {
                fetchCityByName(event.cityName)
            }
            CityListContract.CityListEvent.FetchLocation -> {
                fetchLocationGPS()
            }
            CityListContract.CityListEvent.OnErrorSeen -> _error.update { null }
            is CityListContract.CityListEvent.NavigateToDetailScreen -> {
                sessionRepository.setCoordDetailScreen(event.item.city.coord)
                _action.update { CityListContract.CityListAction.NavigateToDetailScreen }
            }
            CityListContract.CityListEvent.OnActionSeen -> _action.update { null }
        }
    }

    private fun fetchCitiesWeather() {
        viewModelScope.launch {
            _cityList.update { Resource.loading(null) }
            repository.getAllLocation().collect{ response ->
                _cityList.update { response }
            }
        }
    }

    private fun fetchCityByName(name: String){
        viewModelScope.launch {
            repository.getWeather(
                typeRequestWeather = TypeRequestWeather.ByCityName(name = name),
                units = UnitsRequest.Metric,
                langId = Locale.getDefault().displayLanguage,
                justWeatherConditionForTheExactTime = true
            ).collect{ response ->
                when(response.status){
                    Status.LOADING -> _cityList.update { it.copy(status = Status.LOADING) }
                    Status.ERROR -> {
                        _error.update { R.string.no_city_error }
                        _cityList.update { it.copy(status = Status.SUCCESS) }
                    }
                    Status.SUCCESS -> {
                        response.data?.let { newLocation ->
                            handleNewCityResponse(newLocation)
                        }
                    }
                }
            }
        }
    }

    private fun fetchLocationGPS(){
        fetchingLocationGps?.cancel()
        fetchingLocationGps = viewModelScope.launch {
            _cityList.update { it.copy(status = Status.LOADING) }
            locationService.getLocationUpdates(8000).catch {
                _error.update { R.string.something_went_wrong }
                _cityList.update { it.copy(status = Status.ERROR) }
            }.take(1).collect{
                fetchCityByLocation(it)
            }

        }
    }

    private fun fetchCityByLocation(location: Location){
        viewModelScope.launch {
            repository.getWeather(
                typeRequestWeather = TypeRequestWeather.ByLatLon(lat = location.latitude, lon = location.longitude),
                units = UnitsRequest.Metric,
                langId = Locale.getDefault().displayLanguage,
                justWeatherConditionForTheExactTime = true
            ).collect{response ->
                when(response.status){
                    Status.LOADING -> _cityList.update { it.copy(status = Status.LOADING) }
                    Status.ERROR -> {
                        _error.update { R.string.something_went_wrong }
                        _cityList.update { it.copy(status = Status.ERROR) }
                    }
                    Status.SUCCESS -> {
                        response.data?.let { newLocation ->
                            handleNewCityResponse(newLocation)
                        }
                    }
                }
                //fetchingLocationGps?.cancel()
            }
        }
    }

    private fun handleNewCityResponse(newLocation: WeatherInfoPerLocation){
        val existsCity = _cityList.value.data?.find {
            it.city.idCity == newLocation.city.idCity || it.city.coord == newLocation.city.coord
        }
        if (existsCity == null){
            val listCities = _cityList.value.data ?: emptyList()
            _cityList.update {
                it.copy(
                    status = Status.SUCCESS,
                    data = listCities.toMutableList().apply {
                        add(0,newLocation)
                    }.toList()
                )
            }
        }else{ //ALREADY EXIST CITY, UPDATE VALUES
            _cityList.update {
                it.copy(
                    status = Status.SUCCESS,
                    data = it.data?.map { city ->
                        if (city == existsCity)
                            newLocation
                        else
                            city.copy()
                    }?.toList()
                )
            }
        }
    }

    private fun deleteLocation(location: WeatherInfoPerLocation){
        viewModelScope.launch {
            when(repository.deleteLocation(location)){
                true -> {
                    _cityList.update {
                        it.copy(
                            data = it.data?.toMutableList()?.apply {
                                remove(location)
                            }?.toList()
                        )
                    }
                }
                false -> {
                    _error.update { R.string.something_went_wrong }
                }
            }
        }
    }
}
