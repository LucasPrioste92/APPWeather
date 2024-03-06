package com.lucasprioste.weatherapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lucasprioste.weatherapp.utils.ConnectionLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val connectionLiveData: ConnectionLiveData
) : ViewModel() {
    val connection: LiveData<Boolean>
        get() = connectionLiveData
}