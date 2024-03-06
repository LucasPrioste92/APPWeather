package com.lucasprioste.weatherapp.presentation.city_detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lucasprioste.weatherapp.databinding.FragmentCityDetailBinding
import com.lucasprioste.weatherapp.domain.mappers.toAdditionalInfoList
import com.lucasprioste.weatherapp.presentation.city_detail.adapters.AdditionalInfoAdapter
import com.lucasprioste.weatherapp.presentation.city_detail.adapters.WeatherInfoAdapter
import com.lucasprioste.weatherapp.utils.RecyclerViewSpacing
import com.lucasprioste.weatherapp.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CityDetailFragment : Fragment() {

    private val viewModel: CityDetailViewModel by viewModels()
    private lateinit var binding: FragmentCityDetailBinding

    private val additionalInfoAdapter = AdditionalInfoAdapter()
    private val weatherInfoAdapter = WeatherInfoAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCityDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvAdditionalInfo.apply {
            layoutManager = GridLayoutManager(context,3)
            adapter = additionalInfoAdapter
        }

        binding.rvWeatherInfo.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false )
            adapter = weatherInfoAdapter
        }
        binding.rvWeatherInfo.addItemDecoration(RecyclerViewSpacing(horizontal = 25))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onStart() {
        super.onStart()

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.info.collect{ detail ->
                    when(detail.status){
                        Status.LOADING -> binding.progressBar.visibility = VISIBLE
                        Status.ERROR -> binding.progressBar.visibility = GONE
                        Status.SUCCESS -> {
                            detail.data?.let { item ->
                                binding.info.cityName.text = item.city.getCityNameFormatted()
                                binding.info.dayInfo.text = item.info.first().getDateTimeFormatted()
                                binding.info.tempInfo.text = item.info.first().getWeatherInfoFormatted()
                                binding.info.conditionInfo.text = item.info.first().weatherCondition.getDescriptionFormatted()
                                binding.info.imgWeather.setImageDrawable(context?.getDrawable(item.info.first().weatherCondition.icon))

                                weatherInfoAdapter.setInfo(item.info)
                                additionalInfoAdapter.setInfo(item.toAdditionalInfoList())
                            }
                            binding.progressBar.visibility = GONE
                        }
                    }
                }
            }

            launch {
                viewModel.error.collect{
                    it?.let {
                        Toast.makeText(context, getString(it), Toast.LENGTH_LONG).show()
                        viewModel.onEvent(CityDetailContract.CityDetailEvent.OnErrorSeen)
                    }
                }
            }
        }
    }

}