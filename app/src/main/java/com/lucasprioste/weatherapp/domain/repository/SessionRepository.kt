package com.lucasprioste.weatherapp.domain.repository

import com.lucasprioste.weatherapp.domain.models.Coord

interface SessionRepository {
    fun setCoordDetailScreen(cord: Coord)
    fun getCoordDetailScreen(): Coord
}