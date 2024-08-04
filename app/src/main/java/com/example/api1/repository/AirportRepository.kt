package com.example.api1.repository

import android.content.Context
import android.util.Log
import com.example.api1.data.model.Airports
import com.example.api1.data.network.AirportApiClient
import com.example.api1.data.network.ApiService
import com.example.api1.data.preferences.DataStoreManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AirportRepository(private val context: Context) {
    private val apiService: ApiService = AirportApiClient.instance
    private val airportsKey = DataStoreManager.getStringKey("airports")
    private val gson = Gson()

    fun getAirports(onResult: (List<Airports>?) -> Unit, onError: (Throwable) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            // First, try to fetch data locally
            val localData = fetchLocally()
            if (localData != null && localData.isNotEmpty()) {
                Log.d("AirportRepository", "Fetched locally: $localData")
                onResult(localData)
            } else {
                // If no local data, fetch from API
                fetchAirportsFromApi(onResult, onError)
            }
        }
    }

    private fun fetchLocally(): List<Airports>? {
        val savedAirportsJson = DataStoreManager.getData(context, airportsKey)
        Log.d("AirportRepository", "Local JSON: $savedAirportsJson")
        return if (!savedAirportsJson.isNullOrEmpty()) {
            deserializeAirports(savedAirportsJson)
        } else {
            null
        }
    }

    private fun fetchAirportsFromApi(onResult: (List<Airports>?) -> Unit, onError: (Throwable) -> Unit) {
        val call = apiService.getAirports()

        call.enqueue(object : Callback<List<Airports>> {
            override fun onResponse(call: Call<List<Airports>>, response: Response<List<Airports>>) {
                if (response.isSuccessful) {
                    val airports = response.body()
                    Log.d("AirportRepository", "Fetched from API: $airports")
                    // Save the fetched airport data in DataStore
                    CoroutineScope(Dispatchers.IO).launch {
                        val airportsJson = serializeAirports(airports)
                        Log.d("AirportRepository", "Saving to local storage: $airportsJson")
                        DataStoreManager.saveData(context, airportsKey, airportsJson)
                    }
                    onResult(airports)
                } else {
                    onError(Throwable(response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<List<Airports>>, t: Throwable) {
                onError(t)
            }
        })
    }

    private fun serializeAirports(airports: List<Airports>?): String {
        // Serialize the list of airports to JSON
        return gson.toJson(airports)
    }

    private fun deserializeAirports(jsonString: String): List<Airports> {
        val airportListType = object : TypeToken<List<Airports>>() {}.type
        return try {
            val airports: List<Airports> = gson.fromJson(jsonString, airportListType)
            Log.d("AirportRepository", "Deserialized JSON: $airports")
            airports
        } catch (e: Exception) {
            Log.e("AirportRepository", "Failed to parse JSON: ${e.message}")
            emptyList()
        }
    }
}
