package com.example.api1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.api1.data.model.Airports
import com.example.api1.repository.AirportRepository
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
                // Fetch airports using the repository
                val result = withContext(Dispatchers.IO) {
                    val airportsList = mutableListOf<Airports>()
                    var fetchError: Throwable? = null

                    airportRepository.getAirports(
                        onResult = { airports ->
                            airportsList.addAll(airports ?: emptyList())
                        },
                        onError = { error ->
                            fetchError = error
                        }
                    )

                    if (fetchError != null) {
                        throw fetchError!!
                    }
                    airportsList
                }
                _airports.postValue(result)
            } catch (e: Exception) {
                _error.postValue(e.localizedMessage)
            }
        }
    }
}
