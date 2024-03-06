package com.lucasprioste.weatherapp.data.repository

import com.lucasprioste.weatherapp.domain.models.Coord
import com.lucasprioste.weatherapp.domain.repository.SessionRepository

class SessionRepositoryImp: SessionRepository {
    var cord: Coord = Coord(0.0,0.0)

    override fun setCoordDetailScreen(cord: Coord) {
        this.cord = cord
    }

    override fun getCoordDetailScreen(): Coord {
        return cord
    }
}