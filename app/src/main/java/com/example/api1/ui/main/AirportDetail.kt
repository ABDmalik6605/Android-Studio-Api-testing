package com.example.api1.ui.main

import android.os.Bundle
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
        val textViewContact: TextView = findViewById(R.id.itemContact)

        airport?.let {
            textViewName.text = it.name
            textViewLocation.text = "Lat: ${it.location.latitude}, Long: ${it.location.longitude}"
            textViewCode.text = it.code
            textViewCity.text = it.city
            textViewCountry.text = it.country
            textViewTimezone.text = it.timezone
            textViewAirlines.text = it.airlines.joinToString()
            textViewServices.text = it.services.joinToString()
            textViewContact.text = it.contactInfo?.let { contact ->
                "Phone: ${contact.phone}, Email: ${contact.email}, Website: ${contact.website}"
            } ?: "Contact information not available"
        } ?: run {
            textViewName.text = "No data available"
            textViewLocation.text = "Location not found"
        }
    }
}
