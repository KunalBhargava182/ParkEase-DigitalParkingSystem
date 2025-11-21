package com.parkease.app.ui.selection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.parkease.app.R

class ParkingAdapter(
    private val parkingList: List<ParkingArea>,
    private val onItemClick: (ParkingArea) -> Unit
) : RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder>() {

    inner class ParkingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.card_parking)
        val name: TextView = view.findViewById(R.id.tv_parking_name)
        val address: TextView = view.findViewById(R.id.tv_parking_address)
        val distance: TextView = view.findViewById(R.id.tv_parking_distance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parking_area, parent, false)
        return ParkingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParkingViewHolder, position: Int) {
        val item = parkingList[position]
        holder.name.text = item.name
        holder.address.text = item.address
        holder.distance.text = item.distance

        // Click listener
        holder.card.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = parkingList.size
}
