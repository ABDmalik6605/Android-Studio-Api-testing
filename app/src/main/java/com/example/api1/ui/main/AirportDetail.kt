package com.example.api1.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.api1.R
import com.example.api1.data.model.Airports

class AirportDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.airport_detail)

        val airport = intent.getSerializableExtra("airport") as? Airports

        val textViewName: TextView = findViewById(R.id.itemTitle)
        val textViewLocation: TextView = findViewById(R.id.itemLocation)
        val textViewCode: TextView = findViewById(R.id.itemCode)
        val textViewCity: TextView = findViewById(R.id.itemCity)
        val textViewCountry: TextView = findViewById(R.id.itemCountry)
        val textViewTimezone: TextView = findViewById(R.id.itemTimezone)
        val textViewAirlines: TextView = findViewById(R.id.itemAirlines)
        val textViewServices: TextView = findViewById(R.id.itemServices)
        val textViewPhone: TextView = findViewById(R.id.itemPhone)
        val textViewEmail: TextView = findViewById(R.id.itemEmail)
        val textViewWebsite: TextView = findViewById(R.id.itemWebsite)

        airport?.let {
            textViewName.text = it.name
            textViewLocation.text = "Lat: ${it.location.latitude}, Long: ${it.location.longitude}"
            textViewCode.text = it.code
            textViewCity.text = it.city
            textViewCountry.text = it.country
            textViewTimezone.text = it.timezone
            textViewAirlines.text = it.airlines.joinToString()
            textViewServices.text = it.services.joinToString()

            if (it.contact_info != null) {
                textViewPhone.text = it.contact_info.phone
                textViewEmail.text = it.contact_info.email
                textViewWebsite.text = it.contact_info.website
            } else {
                textViewPhone.text = "N/A"
                textViewEmail.text = "N/A"
                textViewWebsite.text = "N/A"
                Log.e("AirportDetail", "ContactInfo is null")
            }
        } ?: run {
            textViewName.text = "No data available"
            textViewLocation.text = "Location not found"
        }
    }
}