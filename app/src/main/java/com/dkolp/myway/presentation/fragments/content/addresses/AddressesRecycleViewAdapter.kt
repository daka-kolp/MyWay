package com.dkolp.myway.presentation.fragments.content.addresses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dkolp.myway.R
import com.dkolp.myway.core.domain.map.entities.Place

class AddressesRecycleViewAdapter(private var items: List<Place> = mutableListOf()) : RecyclerView.Adapter<TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val listItemView = LayoutInflater.from(parent.context).inflate(R.layout.address_item, parent, false)
        return TaskViewHolder(listItemView)
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.address
        holder.location.text = item.geolocation.formatted()
    }
}

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById(R.id.name)
    val location: TextView = itemView.findViewById(R.id.location)
}
