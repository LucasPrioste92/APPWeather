package com.lucasprioste.weatherapp.presentation.city_list

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lucasprioste.weatherapp.R
import com.lucasprioste.weatherapp.databinding.FragmentCityListBinding
import com.lucasprioste.weatherapp.domain.models.WeatherInfoPerLocation
import com.lucasprioste.weatherapp.presentation.city_list.adapters.CityListAdapter
import com.lucasprioste.weatherapp.presentation.city_list.adapters.OnLocalClickListener
import com.lucasprioste.weatherapp.presentation.city_list.adapters.SwipeToDeleteCallback
import com.lucasprioste.weatherapp.utils.RecyclerViewSpacing
import com.lucasprioste.weatherapp.utils.Status
import com.lucasprioste.weatherapp.utils.hasLocationPermission
import com.lucasprioste.weatherapp.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CityListFragment : Fragment(), OnLocalClickListener {
    private lateinit var binding: FragmentCityListBinding
    private val viewModel: CityListViewModel by viewModels()

    private val cityListAdapter = CityListAdapter(this)
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isLocationPermissionGranted = context?.hasLocationPermission() ?: false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCityListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            isLocationPermissionGranted = context?.hasLocationPermission() ?: false
        }

        requestPermission()

        binding.cityList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cityListAdapter
        }
        binding.cityList.addItemDecoration(RecyclerViewSpacing(vertical = 25))
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = cityListAdapter.entries[viewHolder.adapterPosition]
                viewModel.onEvent(CityListContract.CityListEvent.OnDeleteLocation(item))
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.cityList)

        binding.search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val enteredText = binding.search.text.toString()
                viewModel.onEvent(CityListContract.CityListEvent.OnSearch(cityName = enteredText))
                binding.search.let { activity?.hideKeyboard(it) }
                binding.search.text = null
                true
            } else {
                false
            }
        }

        binding.locationBtn.setOnClickListener {
            if (context?.hasLocationPermission() == true){
                viewModel.onEvent(CityListContract.CityListEvent.FetchLocation)
            }else{
                Toast.makeText(context, getString(R.string.no_permission_location), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val context = this.requireContext()
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.cityList.collect { cityList ->
                    Log.v("CityListFragment", "cityList=$cityList")
                    when (cityList.status) {
                        Status.SUCCESS -> {
                            cityList.data?.let {
                                cityListAdapter.setCities(it)
                            }
                            binding.progressBar.visibility = GONE
                        }
                        Status.LOADING -> {
                            binding.progressBar.visibility = VISIBLE
                        }
                        Status.ERROR -> {
                            binding.progressBar.visibility = GONE
                        }
                    }
                }
            }

            launch {
                viewModel.error.collect{ error ->
                    error?.let {
                        Toast.makeText(context, getString(error), Toast.LENGTH_LONG).show()
                        viewModel.onEvent(CityListContract.CityListEvent.OnErrorSeen)
                    }
                }
            }

            launch {
                viewModel.action.collect{ action ->
                    action?.let {
                        when(action){
                            CityListContract.CityListAction.NavigateToDetailScreen -> {
                                findNavController().navigate(R.id.action_CityListFragment_to_cityDetailFragment)
                            }
                        }
                        viewModel.onEvent(CityListContract.CityListEvent.OnActionSeen)
                    }
                }
            }
        }
    }

    override fun onItemClick(item: WeatherInfoPerLocation) {
        viewModel.onEvent(CityListContract.CityListEvent.NavigateToDetailScreen(item))
    }

    private fun requestPermission(){
        isLocationPermissionGranted = context?.hasLocationPermission() ?: false
        if (!isLocationPermissionGranted){
            val permissionRequest = listOf(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
            )
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }
}
