package com.example.api1.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.api1.R
import com.example.api1.adapter.ItemRecyclerViewAdapter
import com.example.api1.data.model.Airports
import com.example.api1.data.model.ItemModel
import com.example.api1.viewmodel.AirportViewModel

class MainActivity : AppCompatActivity() {
    private val airportViewModel: AirportViewModel by viewModels()
    private val itemList = ArrayList<ItemModel>()
    private lateinit var itemRecyclerViewAdapter: ItemRecyclerViewAdapter
    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var endTextView: TextView
    private lateinit var loadingScreen: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
        showLoadingScreen()
        observeAirports()
    }

    private fun observeAirports() {
        airportViewModel.airports.observe(this) { airports ->
            Log.d("MainActivity", "Observing airports: $airports")
            airports?.let {
                itemList.clear()
                it.forEach { airport ->
                    Log.d("MainActivity", "Adding airport: ${airport.name}")
                    itemList.add(ItemModel(airport.name, airport))
                }
                itemRecyclerViewAdapter.notifyDataSetChanged()
                hideLoadingScreen()
            }
        }

        airportViewModel.error.observe(this, Observer { error ->
            Log.e("MainActivity", "Error: $error")
            hideLoadingScreen()
        })
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

    fun onItemClick(airport: Airports) {
        val intent = Intent(this, AirportDetail::class.java).apply {
            putExtra("airport", airport)
        }
        startActivity(intent)
    }
}
