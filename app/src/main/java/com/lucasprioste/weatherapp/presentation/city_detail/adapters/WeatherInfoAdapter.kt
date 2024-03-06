package com.lucasprioste.weatherapp.presentation.city_detail.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lucasprioste.weatherapp.databinding.WeatherInfoItemBinding
import com.lucasprioste.weatherapp.domain.models.InfoPerTime

class WeatherInfoAdapter : RecyclerView.Adapter<WeatherInfoAdapter.WeatherItemViewHolder>() {

    var entries = mutableListOf<InfoPerTime>()
    fun setInfo(newEntries: List<InfoPerTime>) {
        this.entries = newEntries.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): WeatherItemViewHolder {
        val binding = WeatherInfoItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return WeatherItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: WeatherItemViewHolder, position: Int) {
        val item = entries[position]
        viewHolder.bind(item)
    }

    override fun getItemCount() = entries.size

    class WeatherItemViewHolder(private val binding: WeatherInfoItemBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item: InfoPerTime){
            binding.txtTime.text = item.getTimeFormatted()
            binding.txtTemperature.text = item.main.tempFormatted()
            binding.icon.setImageDrawable(itemView.context.getDrawable(item.weatherCondition.icon))
            binding.txtDate.text = item.getDateDayAndMonth()
        }
    }
}