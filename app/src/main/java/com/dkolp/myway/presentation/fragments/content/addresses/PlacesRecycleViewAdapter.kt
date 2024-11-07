package com.dkolp.myway.presentation.fragments.content.addresses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dkolp.myway.R
import com.dkolp.myway.core.domain.entities.Place

class PlacesRecycleViewAdapter(private var items: List<Place> = mutableListOf()) : RecyclerView.Adapter<PlaceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val listItemView = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        return PlaceViewHolder(listItemView)
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val item = items[position]
        val address = item.address
        holder.name.text = address.toString()
        holder.type.text = item.type
        holder.location.text = address.geolocation.formatted()
    }
}

class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById(R.id.name)
    val type: TextView = itemView.findViewById(R.id.type)
    val location: TextView = itemView.findViewById(R.id.location)
}
