package com.lucasprioste.weatherapp.presentation.city_detail

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CityDetailViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val sessionRepository: SessionRepository
): ViewModel() {

    private val _info = MutableStateFlow<Resource<WeatherInfoPerLocation>>(Resource.loading(null))
    val info = _info.asStateFlow()

    private val _error = MutableStateFlow<Int?>(null)
    val error = _error.asStateFlow()

    init {
        viewModelScope.launch {
            val coord = sessionRepository.getCoordDetailScreen()
            repository.getWeather(
                typeRequestWeather = TypeRequestWeather.ByLatLon(
                    lat = coord.lat, lon = coord.lon
                ),
                units = UnitsRequest.Metric,
                langId = Locale.getDefault().displayLanguage,
                justWeatherConditionForTheExactTime = false
            ).collect{ response ->
                when(response.status){
                    Status.LOADING -> _info.update { it.copy(status = Status.LOADING) }
                    Status.ERROR -> {
                        _error.update { R.string.something_went_wrong }
                        _info.update { it.copy(status = Status.ERROR) }
                    }
                    Status.SUCCESS -> {
                        _info.update {
                            it.copy(
                                status = Status.SUCCESS,
                                data = response.data
                            )
                        }
                    }
                }
            }
        }
    }

    fun onEvent(event: CityDetailContract.CityDetailEvent){
        when(event){
            CityDetailContract.CityDetailEvent.OnErrorSeen -> _error.update { null }
        }
    }
}