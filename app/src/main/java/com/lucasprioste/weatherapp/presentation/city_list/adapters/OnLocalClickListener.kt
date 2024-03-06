package com.lucasprioste.weatherapp.presentation.city_list.adapters

import com.lucasprioste.weatherapp.domain.models.WeatherInfoPerLocation

interface OnLocalClickListener {
    fun onItemClick(item: WeatherInfoPerLocation)
}