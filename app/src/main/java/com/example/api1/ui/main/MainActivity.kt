package com.example.api1.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.api1.R
import com.example.api1.data.model.Airports
import com.example.api1.data.model.ItemModel
import com.example.api1.preferences.DataStoreManager
import com.example.api1.repository.AirportRepository
import com.example.try11_1.adapter.ItemRecyclerViewAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), ItemRecyclerViewAdapter.OnItemClickListener {
    private val itemList = ArrayList<ItemModel>()
    private lateinit var itemRecyclerViewAdapter: ItemRecyclerViewAdapter
    private lateinit var itemRecyclerView: RecyclerView
    private val airportRepository = AirportRepository()
    private lateinit var endTextView: TextView
    private val gson = Gson()
    private val airportsKey = stringPreferencesKey("airports")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
        fetchAirports()
    }

    private fun initUI() {
        endTextView = findViewById(R.id.end)
        itemRecyclerView = findViewById(R.id.recyclerView)
        itemRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    endTextView.visibility = View.VISIBLE
                } else {
                    endTextView.visibility = View.GONE
                }
            }
        })
        itemRecyclerView.layoutManager = LinearLayoutManager(this)
        itemRecyclerViewAdapter = ItemRecyclerViewAdapter(itemList, this)
        itemRecyclerView.adapter = itemRecyclerViewAdapter
    }

    private fun fetchAirports() {
        lifecycleScope.launch {
            val savedAirportsJson = DataStoreManager.getData(this@MainActivity, airportsKey) ?: ""
            if (savedAirportsJson.isNotEmpty()) {
                val airports: List<Airports> = deserializeAirports(savedAirportsJson)
                airports.forEach { airport ->
                    Log.d(
                        "MainActivity",
                        "Airport: ${airport.name}, Code: ${airport.code}, ID: ${airport.id}"
                    )
                }
                updateUI(airports)
            } else {
                airportRepository.getAirports(onResult = { airports ->
                    airports?.forEach { airport ->
                        Log.d(
                            "MainActivity",
                            "Airport: ${airport.name}, Code: ${airport.code}, ID: ${airport.id}"
                        )
                    }
                    val airportsJson = serializeAirports(airports)
                    lifecycleScope.launch {
                        DataStoreManager.saveData(this@MainActivity, airportsKey, airportsJson)
                    }
                    updateUI(airports)
                }, onError = { error ->
                    Log.e("MainActivity", "Error: ${error.message}")
                })
            }
        }
    }

    private fun updateUI(airports: List<Airports>?) {
        airports?.let {
            itemList.clear()
            it.forEach { airport ->
                itemList.add(ItemModel(airport.name, airport))
            }
            itemRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    override fun onItemClick(airport: Airports) {
        // Handle the item click event here
        Log.d("MainActivity", "Clicked on: ${airport.name}")
        // You can start a new activity or show details here
        val intent = Intent(this, AirportDetail::class.java)
        intent.putExtra("airport", airport)
        startActivity(intent)
    }

    private fun serializeAirports(airports: List<Airports>?): String {
        return gson.toJson(airports)
    }

    private fun deserializeAirports(data: String): List<Airports> {
        return gson.fromJson(data, object : TypeToken<List<Airports>>() {}.type)
    }
}
