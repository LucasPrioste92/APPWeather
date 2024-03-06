package com.lucasprioste.weatherapp.presentation.city_detail

sealed class CityDetailContract{
    sealed class CityDetailEvent{
        object OnErrorSeen: CityDetailEvent()
    }

    sealed class CityDetailAction{

    }
}
