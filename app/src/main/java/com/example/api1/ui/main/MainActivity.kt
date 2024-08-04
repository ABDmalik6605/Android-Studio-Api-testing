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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.api1.R
import com.example.api1.adapter.ItemRecyclerViewAdapter
import com.example.api1.data.model.Airports
import com.example.api1.data.model.ItemModel
import com.example.api1.repository.AirportRepository

class MainActivity : AppCompatActivity() {
    private val itemList = ArrayList<ItemModel>()
    private lateinit var itemRecyclerViewAdapter: ItemRecyclerViewAdapter
    private lateinit var itemRecyclerView: RecyclerView
    private val airportRepository = AirportRepository()
    private lateinit var endTextView: TextView


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
        airportRepository.getAirports(
            onResult = { airports ->
                airports?.let {
                    itemList.clear()
                    it.forEach { airport ->
                        Log.d("MainActivity", "Fetched airport: $airport")
                        itemList.add(ItemModel(airport.name, airport))
                    }
                    itemRecyclerViewAdapter.notifyDataSetChanged()
                }
            },
            onError = { error ->
                Log.e("MainActivity", "Error: ${error.message}")
            }
        )
    }

    fun onItemClick(airport: Airports) {
        val intent = Intent(this, AirportDetail::class.java).apply {
            putExtra("airport", airport)
        }
        startActivity(intent)
    }

}
