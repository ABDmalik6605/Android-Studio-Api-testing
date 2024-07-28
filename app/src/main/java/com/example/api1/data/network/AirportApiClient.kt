package com.example.api1.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AirportApiClient {
    private const val BASE_URL = "https://freetestapi.com/api/v1/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
