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
            if (localData != null) {
                onResult(localData)
            } else {
                // If no local data, fetch from API
                fetchAirportsFromApi(onResult, onError)
            }
        }
    }
    private fun fetchLocally(): List<Airports>? {
        val savedStudentsJson = DataStoreManager.getData(context, airportsKey)
        return if (!savedStudentsJson.isNullOrEmpty()) {
            deserializeAirports(savedStudentsJson)
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
                    Log.d("AirportRepository", "API Response: $airports")
                    // Proceed with saving and processing
                } else {
                    Log.e("AirportRepository", "API Error: ${response.errorBody()?.string()}")
                    onError(Throwable(response.errorBody()?.string()))
                }
            }


            override fun onFailure(call: Call<List<Airports>>, t: Throwable) {
                Log.e("AirportRepository", "API call failed: ${t.message}")
                onError(t)
            }
        })
    }


    private fun serializeAirports(airports: List<Airports>?): String {
        // Serialize the list of students to JSON
        return gson.toJson(airports)
    }

    private fun deserializeAirports(data: String): List<Airports> {
        // Deserialize the JSON string to a list of students
        return gson.fromJson(data, object : TypeToken<List<Airports>>() {}.type)
    }
//    fun getLocationByName(name: String): Location? {
//        return airportData?.find { it.name == name }?.location
//    }
}