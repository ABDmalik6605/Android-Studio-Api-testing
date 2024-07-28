package com.example.api1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.api1.R
import com.example.api1.data.model.Airports
import com.example.api1.data.model.ItemModel
import com.example.api1.ui.main.MainActivity

class ItemRecyclerViewAdapter(
    private val itemList: List<ItemModel>,
    private val activity: MainActivity
) : RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.itemTitle1)

        init {
            view.setOnClickListener {
                val item = itemList[adapterPosition]
                activity.onItemClick(item.airport)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.title.text = item.name
    }

    override fun getItemCount(): Int = itemList.size
}
