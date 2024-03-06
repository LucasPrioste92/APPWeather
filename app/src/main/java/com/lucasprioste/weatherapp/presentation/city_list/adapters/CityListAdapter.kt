package com.lucasprioste.weatherapp.presentation.city_list.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lucasprioste.weatherapp.databinding.WeatherLocationItemBinding
import com.lucasprioste.weatherapp.domain.models.WeatherInfoPerLocation

class CityListAdapter(private val listener: OnLocalClickListener): RecyclerView.Adapter<CityListAdapter.CityItemViewHolder>() {

    var entries = mutableListOf<WeatherInfoPerLocation>()
    fun setCities(newEntries: List<WeatherInfoPerLocation>) {
        this.entries = newEntries.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CityItemViewHolder {
        val binding = WeatherLocationItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return CityItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: CityItemViewHolder, position: Int) {
        val item = entries[position]
        viewHolder.bind(item,listener)
    }

    override fun getItemCount() = entries.size

    class CityItemViewHolder(private val binding: WeatherLocationItemBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item: WeatherInfoPerLocation, listener: OnLocalClickListener){
            binding.cityName.text = item.city.getCityNameFormatted()
            binding.dayInfo.text = item.info.first().getDateTimeFormatted()
            binding.tempInfo.text = item.info.first().getWeatherInfoFormatted()
            binding.conditionInfo.text = item.info.first().weatherCondition.getDescriptionFormatted()
            binding.imgWeather.setImageDrawable(itemView.context.getDrawable(item.info.first().weatherCondition.icon))

            binding.root.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }
}


