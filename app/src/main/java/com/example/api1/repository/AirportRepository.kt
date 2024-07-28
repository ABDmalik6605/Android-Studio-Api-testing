package com.example.api1.repository

import com.example.api1.data.model.Airports
import com.example.api1.data.model.Location
import com.example.api1.data.network.AirportApiClient
import com.example.api1.data.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AirportRepository {

    private val apiService: ApiService = AirportApiClient.instance
    private var airportData: List<Airports>? = null

    fun getAirports(onResult: (List<Airports>?) -> Unit, onError: (Throwable) -> Unit) {
        val call = apiService.getStudents()

        call.enqueue(object : Callback<List<Airports>> {
            override fun onResponse(call: Call<List<Airports>>, response: Response<List<Airports>>) {
                if (response.isSuccessful) {
                    airportData = response.body()
                    onResult(response.body())
                } else {
                    onError(Throwable(response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<List<Airports>>, t: Throwable) {
                onError(t)
            }
        })
    }

    fun getLocationByName(name: String): Location? {
        return airportData?.find { it.name == name }?.location
    }
}
