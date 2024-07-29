package com.example.api1.data.network

import com.example.api1.data.model.Airports
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("airports")
    fun getAirports(): Call<List<Airports>>

    // ALL METHOD SHOULD BE MENTIONED HERE
    // PUT
    // POST
    // GET
}
