package com.lucasprioste.weatherapp.data.mapper

import com.lucasprioste.weatherapp.R
import com.lucasprioste.weatherapp.data.remote.dto.CityDto
import com.lucasprioste.weatherapp.data.remote.dto.CoordDto
import com.lucasprioste.weatherapp.data.remote.dto.InfoDto
import com.lucasprioste.weatherapp.data.remote.dto.MainDto
import com.lucasprioste.weatherapp.data.remote.dto.WeatherDto
import com.lucasprioste.weatherapp.data.remote.dto.WeatherResponseDto
import com.lucasprioste.weatherapp.data.remote.dto.WindDto
import com.lucasprioste.weatherapp.domain.models.City
import com.lucasprioste.weatherapp.domain.models.ConditionWeather
import com.lucasprioste.weatherapp.domain.models.Coord
import com.lucasprioste.weatherapp.domain.models.InfoPerTime
import com.lucasprioste.weatherapp.domain.models.MainInfo
import com.lucasprioste.weatherapp.domain.models.WeatherInfoPerLocation
import com.lucasprioste.weatherapp.domain.models.Wind
import com.lucasprioste.weatherapp.utils.WeatherStatusCondition
import com.lucasprioste.weatherapp.utils.getLocalDateTime

fun WeatherResponseDto.toWeatherInfoPerLocation(): WeatherInfoPerLocation {
    return WeatherInfoPerLocation(
        city = city.toCity(),
        info = list.map { it.toInfoPerTime() }
    )
}

fun CityDto.toCity(): City {
    return City(
        coord = coord.toCoord(),
        country = country,
        name = name,
        sunrise = getLocalDateTime(value = sunrise),
        sunset= getLocalDateTime(value = sunset),
        timezone = timezone,
        idCity = id
    )
}

fun CoordDto.toCoord(): Coord {
    return Coord(
        lat = lat,
        lon = lon
    )
}

fun InfoDto.toInfoPerTime(): InfoPerTime {
    return InfoPerTime(
        dateTime = getLocalDateTime(dt),
        isDay = sys.pod.uppercase() == "D", //N - night, D - day
        precipitationProbability = pop,
        visibility = visibility, //Metres
        weatherCondition = weather.firstOrNull()?.toConditionWeather() ?: ConditionWeather(),
        main = main.toMainInfo(),
        wind = wind.toWind()
    )
}

fun WindDto.toWind(): Wind {
    return Wind(
        deg = deg,
        gust = gust,
        speed = speed
    )
}

fun MainDto.toMainInfo(): MainInfo {
    return MainInfo(
        feelsLike = feels_like,
        atmosphericGroundLevel = grnd_level,
        humidity = humidity,
        pressure = pressure,
        atmosphericSeaLevel = sea_level,
        temp = temp,
        tempMax = temp_max,
        tempMin = temp_min
    )
}

fun WeatherDto.toConditionWeather(): ConditionWeather{
    val status: WeatherStatusCondition
    val icon: Int

    when (id) {
        in 200 .. 299 -> {
            status = WeatherStatusCondition.Thunderstorm
            icon = R.drawable.thunderstorm
        }
        in 300 .. 399 -> {
            status = WeatherStatusCondition.Drizzle
            icon = R.drawable.cloudy
        }
        in 500 .. 599 -> {
            status = WeatherStatusCondition.Rain
            icon = R.drawable.rain
        }
        in 600 .. 699 -> {
            status = WeatherStatusCondition.Snow
            icon = R.drawable.snow
        }
        in 700 .. 799 -> {
            status = WeatherStatusCondition.Atmosphere
            icon = R.drawable.wind
        }
        800 -> {
            status = WeatherStatusCondition.Clear
            icon = R.drawable.sun
        }
        in 801 .. 804 -> {
            status = WeatherStatusCondition.Clouds
            icon = R.drawable.clouds
        }
        else -> {
            status = WeatherStatusCondition.Clear
            icon = R.drawable.sun
        }
    }

    return ConditionWeather(
        description = description,
        status = status,
        icon = icon
    )
}