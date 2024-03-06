package com.lucasprioste.weatherapp.presentation.city_list

import com.lucasprioste.weatherapp.domain.models.WeatherInfoPerLocation

sealed class CityListContract{
    sealed class CityListEvent{
        data class OnSearch(val cityName: String): CityListEvent()
        object FetchLocation: CityListEvent()
        data class OnDeleteLocation(val item: WeatherInfoPerLocation): CityListEvent()
        data class NavigateToDetailScreen(val item: WeatherInfoPerLocation): CityListEvent()
        object OnErrorSeen: CityListEvent()
        object OnActionSeen: CityListEvent()
    }

    sealed class CityListAction{
        object NavigateToDetailScreen: CityListAction()
    }
}
