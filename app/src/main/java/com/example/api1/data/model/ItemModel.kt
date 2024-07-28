package com.example.api1.data.model

import java.io.Serializable

data class ItemModel(
    val name: String,
    val airport: Airports
) : Serializable
