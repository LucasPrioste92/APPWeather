package com.lucasprioste.weatherapp.presentation

import com.lucasprioste.weatherapp.MainDispatcherRule
import com.lucasprioste.weatherapp.presentation.city_list.CityListContract
import com.lucasprioste.weatherapp.presentation.city_list.CityListViewModel
import com.lucasprioste.weatherapp.data.FakeLocationClient
import com.lucasprioste.weatherapp.data.FakeSessionRepository
import com.lucasprioste.weatherapp.data.FakeWeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CityListViewModelTest {
    private lateinit var viewModel: CityListViewModel
    private val fakeSessionRepository = FakeSessionRepository()
    private val fakeWeatherRepository = FakeWeatherRepository()
    private val fakeLocationClient = FakeLocationClient()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup(){
        viewModel = CityListViewModel(
            repository = fakeWeatherRepository,
            locationService = fakeLocationClient,
            sessionRepository = fakeSessionRepository
        )
    }

    @Test
    fun `First Init`(): Unit = runBlocking {
        //Assert that values init correctly
        assert(viewModel.error.value == null)
        assert(!viewModel.cityList.value.data.isNullOrEmpty())
        assert(viewModel.action.value == null)

        //Assert that Locations fetch correct from DB
        val locationsVM = viewModel.cityList.value.data!!
        assert(locationsVM.size == fakeWeatherRepository.mockDataDBLocation.size)
        locationsVM.map { loc ->
            val findLocals = locationsVM.filter {
                it.city.idCity == loc.city.idCity ||
                (it.city.coord.lon == loc.city.coord.lon && it.city.coord.lat == loc.city.coord.lat)
            }
            assert(findLocals.size == 1) //No repeated Locals
        }
    }

    @Test
    fun `Event OnDeleteLocation` (): Unit = runBlocking {
        assert(!viewModel.cityList.value.data.isNullOrEmpty())

        val locationsVM = viewModel.cityList.value.data!!
        viewModel.onEvent(CityListContract.CityListEvent.OnDeleteLocation(locationsVM.first()))

        assert(locationsVM.size != viewModel.cityList.value.data!!.size) //Confirm Location Has been deleted
        assert(viewModel.cityList.value.data!!.size == fakeWeatherRepository.mockDataDBLocation.size)
    }

    @Test
    fun `Event FetchLocation` (): Unit = runBlocking {
        assert(!viewModel.cityList.value.data.isNullOrEmpty())

        val locationsVM = viewModel.cityList.value.data!!
        val lastFirst = locationsVM.first()
        viewModel.onEvent(CityListContract.CityListEvent.FetchLocation)

        val newLocationList = viewModel.cityList.value.data!!.first()

        assert(lastFirst.city.coord != newLocationList.city.coord) //Confirm New Location has been added to top
        assert(locationsVM.size != fakeWeatherRepository.mockDataDBLocation.size)
    }

    @Test
    fun `Event OnSearch` (): Unit = runBlocking {
        assert(!viewModel.cityList.value.data.isNullOrEmpty())

        val locationsVM = viewModel.cityList.value.data!!
        val lastFirst = locationsVM.first()
        viewModel.onEvent(CityListContract.CityListEvent.OnSearch("Porto"))

        val newLocationList = viewModel.cityList.value.data!!

        assert(lastFirst.city.coord != newLocationList.first().city.coord) //Confirm New Location has been added to top
        assert(locationsVM.size != fakeWeatherRepository.mockDataDBLocation.size)

        //Assert that is not possible have 2 same city
        viewModel.onEvent(CityListContract.CityListEvent.OnSearch("Porto"))

        val listAfterRequest = viewModel.cityList.value.data!!
        assert(listAfterRequest.size == newLocationList.size)
    }

    @Test
    fun `Event NavigateToDetailScreen` (): Unit = runBlocking {
        assert(viewModel.action.value == null)
        val locationsVM = viewModel.cityList.value.data!!
        viewModel.onEvent(CityListContract.CityListEvent.NavigateToDetailScreen(locationsVM.first()))

        //Confirm Navigate Action Send And Coord set in Session repository to be consumed in Detail Screen
        assert(viewModel.action.value == CityListContract.CityListAction.NavigateToDetailScreen)
        assert(fakeSessionRepository.coord == locationsVM.first().city.coord)
    }
}