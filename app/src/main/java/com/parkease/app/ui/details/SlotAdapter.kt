package com.parkease.app.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.parkease.app.R
import com.parkease.app.util.PricingUtil
import java.text.NumberFormat
import java.util.Locale

class SlotAdapter(
    private val slotList: List<ParkingSlot>,
    private val onItemClick: (ParkingSlot) -> Unit
) : RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    inner class SlotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.card_slot)
        val tvSlot: TextView = view.findViewById(R.id.tv_slot_number)
        val tvSub: TextView = view.findViewById(R.id.tv_slot_sub)
        val tvBadge: TextView = view.findViewById(R.id.tv_badge)
        val tvPrice: TextView = view.findViewById(R.id.tv_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parking_slot, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {

        val slot = slotList[position]

        holder.tvSlot.text = "Slot ${slot.id}"
        holder.tvSub.text = if (!slot.isAvailable) "Booked" else "2 min walk"

        // Clean dynamic pricing
        val (price, isPeak) = PricingUtil.computePrice(slot.basePrice.toDouble())

        // ₹ price clean formatting
        val priceText = "₹$price"
        holder.tvPrice.text = priceText

        // Price color (green normal / red peak)
        holder.tvPrice.setTextColor(
            if (isPeak)
                ContextCompat.getColor(holder.itemView.context, R.color.error_red)
            else
                ContextCompat.getColor(holder.itemView.context, R.color.success_green)
        )

        // Badge states
        holder.tvBadge.text = when {
            !slot.isAvailable -> "BOOKED"
            slot.isSelected -> "SELECTED"
            isPeak -> "PEAK"
            else -> "AVAILABLE"
        }

        // Icon tint
        val tintColor = when {
            !slot.isAvailable -> ContextCompat.getColor(
                holder.itemView.context,
                R.color.slot_booked
            )

            slot.isSelected -> ContextCompat.getColor(
                holder.itemView.context,
                R.color.slot_selected
            )

            else -> ContextCompat.getColor(holder.itemView.context, R.color.teal_dark)
        }

        // Enable/Disable click
        holder.itemView.isEnabled = slot.isAvailable
        holder.card.isEnabled = slot.isAvailable

        // Selection visual
        holder.itemView.isSelected = slot.isSelected

        // Click event
        holder.itemView.setOnClickListener {
            if (slot.isAvailable) {
                onItemClick(slot)
            }
        }
    }

    override fun getItemCount(): Int = slotList.size
}
