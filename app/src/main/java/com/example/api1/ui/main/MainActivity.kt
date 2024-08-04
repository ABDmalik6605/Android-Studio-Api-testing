package com.example.api1.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.api1.R
import com.example.api1.adapter.ItemRecyclerViewAdapter
import com.example.api1.data.model.Airports
import com.example.api1.data.model.ItemModel
import com.example.api1.data.preferences.PreferenceManager
import com.example.api1.repository.AirportRepository
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private val itemList = ArrayList<ItemModel>()
    private lateinit var itemRecyclerViewAdapter: ItemRecyclerViewAdapter
    private lateinit var itemRecyclerView: RecyclerView
    private val airportRepository = AirportRepository()
    private lateinit var endTextView: TextView
    private lateinit var loadingScreen: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.init(this) // Initialize here
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
        showLoadingScreen() // Show loading screen when starting to fetch data
        fetchAirportsFromPreferences()
    }

    private fun initUI() {
        endTextView = findViewById(R.id.end)
        itemRecyclerView = findViewById(R.id.recyclerView)
        loadingScreen = findViewById(R.id.loading_screen)

        itemRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                endTextView.visibility = if (!recyclerView.canScrollVertically(1)) View.VISIBLE else View.GONE
            }
        })
        itemRecyclerView.layoutManager = LinearLayoutManager(this)
        itemRecyclerViewAdapter = ItemRecyclerViewAdapter(itemList, this)
        itemRecyclerView.adapter = itemRecyclerViewAdapter
    }

    private fun showLoadingScreen() {
        loadingScreen.visibility = View.VISIBLE
    }

    private fun hideLoadingScreen() {
        loadingScreen.visibility = View.GONE
    }

    private fun fetchAirportsFromPreferences() {
        val savedAirportsJson = PreferenceManager.get("airports", "")
        if (!savedAirportsJson.isNullOrEmpty()) {
            val airports: List<Airports> = deserializeAirports(savedAirportsJson)
            itemList.clear()
            airports.forEach { airport ->
                itemList.add(ItemModel(airport.name, airport))
            }
            itemRecyclerViewAdapter.notifyDataSetChanged()
            hideLoadingScreen()
        } else {
            fetchAirports()
        }
    }

    private fun fetchAirports() {
        Handler(Looper.getMainLooper()).postDelayed({
            airportRepository.getAirports(
                onResult = { airports ->
                    airports?.let {
                        itemList.clear()
                        it.forEach { airport ->
                            itemList.add(ItemModel(airport.name, airport))
                        }
                        itemRecyclerViewAdapter.notifyDataSetChanged()
                        saveAirportsToPreferences(airports)
                        hideLoadingScreen()
                    }
                },
                onError = { error ->
                    Log.e("MainActivity", "Error: ${error.message}")
                    hideLoadingScreen()
                }
            )
        }, 2000)
    }

    private fun saveAirportsToPreferences(airports: List<Airports>) {
        val airportsJson = serializeAirports(airports)
        PreferenceManager.save("airports", airportsJson)
    }

    private fun serializeAirports(airports: List<Airports>?): String {
        return PreferenceManager.gson.toJson(airports)
    }

    private fun deserializeAirports(data: String): List<Airports> {
        return PreferenceManager.gson.fromJson(data, object : TypeToken<List<Airports>>() {}.type)
    }

    fun onItemClick(airport: Airports) {
        val intent = Intent(this, AirportDetail::class.java).apply {
            putExtra("airport", airport)
        }
        startActivity(intent)
    }
}
