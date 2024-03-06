package com.lucasprioste.weatherapp.presentation

import com.lucasprioste.weatherapp.MainDispatcherRule
import com.lucasprioste.weatherapp.domain.models.Coord
import com.lucasprioste.weatherapp.presentation.city_detail.CityDetailViewModel
import com.lucasprioste.weatherapp.data.FakeSessionRepository
import com.lucasprioste.weatherapp.data.FakeWeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CityDetailViewModelTest {
    private lateinit var viewModel: CityDetailViewModel
    private val fakeWeatherRepository = FakeWeatherRepository()
    private val fakeSessionRepository = FakeSessionRepository()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup(){
        val cord = Coord(
            lat = fakeWeatherRepository.mockDataDBLocation.first().lat,
            lon = fakeWeatherRepository.mockDataDBLocation.first().lon
        )
        fakeSessionRepository.setCoordDetailScreen(cord)

        viewModel = CityDetailViewModel(
            repository = fakeWeatherRepository,
            sessionRepository = fakeSessionRepository
        )
    }

    @Test
    fun `First Init`(): Unit = runBlocking {
        //Assert that values init correctly
        assert(viewModel.error.value == null)
        assert(viewModel.info.value.data != null)
    }
}