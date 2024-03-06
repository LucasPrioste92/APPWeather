package com.lucasprioste.weatherapp.data

import com.lucasprioste.weatherapp.domain.models.Coord
import com.lucasprioste.weatherapp.domain.repository.SessionRepository

class FakeSessionRepository: SessionRepository {
    var coord = Coord(lat = 0.0, lon = 0.0)

    override fun setCoordDetailScreen(cord: Coord) {
        coord = cord
    }

    override fun getCoordDetailScreen(): Coord {
        return coord
    }
}