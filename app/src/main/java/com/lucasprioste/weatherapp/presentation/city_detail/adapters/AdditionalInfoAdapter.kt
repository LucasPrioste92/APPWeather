package com.lucasprioste.weatherapp.presentation.city_detail.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lucasprioste.weatherapp.databinding.AdditionalInfoItemBinding
import com.lucasprioste.weatherapp.domain.models.AdditionalInfo

class AdditionalInfoAdapter : RecyclerView.Adapter<AdditionalInfoAdapter.AdditionalItemViewHolder>() {

    var entries = mutableListOf<AdditionalInfo>()
    fun setInfo(newEntries: List<AdditionalInfo>) {
        this.entries = newEntries.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AdditionalItemViewHolder {
        val binding = AdditionalInfoItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return AdditionalItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: AdditionalItemViewHolder, position: Int) {
        val item = entries[position]
        viewHolder.bind(item)
    }

    override fun getItemCount() = entries.size

    class AdditionalItemViewHolder(private val binding: AdditionalInfoItemBinding): RecyclerView.ViewHolder(binding.root){
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item: AdditionalInfo){
            binding.txtDescription.text = itemView.context.getText(item.description)
            binding.txtValue.text = item.value
            binding.icon.setImageDrawable(itemView.context.getDrawable(item.iconId))
        }
    }
}