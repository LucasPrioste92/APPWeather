package com.lucasprioste.weatherapp

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lucasprioste.weatherapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        viewModel.connection.observe(this){
            if (it){
                binding.navHostFragmentContentMain.visibility = View.VISIBLE
                binding.txtError.visibility = View.GONE
            }else{
                binding.navHostFragmentContentMain.visibility = View.GONE
                binding.txtError.visibility = View.VISIBLE
                binding.txtError.text = this.getText(R.string.no_network)
            }
        }
    }
}
