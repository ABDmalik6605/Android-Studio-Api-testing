package com.example.api1.viewmodel

import AirportRepository
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.api1.data.model.Airports
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AirportViewModel(application: Application) : AndroidViewModel(application) {
    private val airportRepository = AirportRepository(application)
    private val _airports = MutableLiveData<List<Airports>>()
    val airports: LiveData<List<Airports>> get() = _airports
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        fetchAirports()
    }

    private fun fetchAirports() {
        viewModelScope.launch {
            try {
                Log.d("AirportViewModel", "Starting to fetch airports")

                val result = withContext(Dispatchers.IO) {
                    Log.d("AirportViewModel", "Inside withContext block")
                    val airportsList = mutableListOf<Airports>()
                    var fetchError: Throwable? = null

                    airportRepository.getAirports(
                        onResult = { airports ->
                            Log.d("AirportViewModel", "OnResult callback triggered")
                            Log.d("AirportViewModel", "Airports fetched: $airports")
                            airportsList.addAll(airports ?: emptyList())
                        },

                        onError = { error ->
                            Log.e("AirportViewModel", "Error callback triggered: ${error.message}")
                            fetchError = error
                        }
                    )

                    if (fetchError != null) {
                        throw fetchError!!
                    }
                    airportsList
                }
                Log.d("AirportViewModelResult", "Fetched airports: $result")
                _airports.postValue(result)
            } catch (e: Exception) {
                Log.e("AirportViewModel", "Exception in fetchAirports: ${e.message}")
                _error.postValue(e.message)
            }
        }
    }

}
