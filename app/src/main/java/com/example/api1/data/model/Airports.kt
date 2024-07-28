package com.example.api1.data.model

import java.io.Serializable

data class Airports(
    val id: Int,
    val name: String,
    val code: String,
    val location: Location,
    val city: String,
    val country: String,
    val timezone: String,
    val airlines: List<String>,
    val services: List<String>,
    val contactInfo: ContactInfo
) : Serializable

data class Location(
    val latitude: Double,
    val longitude: Double
) : Serializable

data class ContactInfo(
    val phone: String,
    val email: String,
    val website: String
) : Serializable
