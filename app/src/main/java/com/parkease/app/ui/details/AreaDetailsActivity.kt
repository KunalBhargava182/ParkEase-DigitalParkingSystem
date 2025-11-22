package com.parkease.app.ui.details

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.parkease.app.R
import com.parkease.app.ui.booking.BookingConfirmationActivity
import com.parkease.app.util.PricingUtil

// Clean base price slot model
data class ParkingSlot(
    val id: Int,
    var isAvailable: Boolean,
    var basePrice: Int = 20,
    var isSelected: Boolean = false
)

class AreaDetailsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SlotAdapter
    private lateinit var btnBook: Button
    private lateinit var tvParkingName: TextView

    private var selectedSlot: ParkingSlot? = null
    private var selectedIndex: Int = -1
    private lateinit var parkingName: String
    private val slots = mutableListOf<ParkingSlot>()

    companion object {
        private const val KEY_SELECTED_INDEX = "key_selected_index"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area_details)

        tvParkingName = findViewById(R.id.tv_parking_name)
        recyclerView = findViewById(R.id.recycler_slots)
        btnBook = findViewById(R.id.btn_book_slot)

        parkingName = intent.getStringExtra("parking_name") ?: "Parking Area"
        tvParkingName.text = parkingName

        val basePrices = listOf(20, 30, 40, 50, 60)

        // Initialize slots
        if (savedInstanceState == null) {
            for (i in 1..10) {
                val available = (i % 3 != 0)
                val base = basePrices[(i - 1) % basePrices.size]
                slots.add(ParkingSlot(i, available, base))
            }
        } else {
            selectedIndex = savedInstanceState.getInt(KEY_SELECTED_INDEX, -1)

            for (i in 1..10) {
                val available = (i % 3 != 0)
                val base = basePrices[(i - 1) % basePrices.size]
                val slot = ParkingSlot(i, available, base)

                if (i - 1 == selectedIndex && slot.isAvailable) {
                    slot.isSelected = true
                    selectedSlot = slot
                }
                slots.add(slot)
            }
        }

        btnBook.isEnabled = selectedSlot != null

        adapter = SlotAdapter(slots) { slotClicked ->

            val index = slots.indexOf(slotClicked)
            if (index == -1) return@SlotAdapter

            if (!slotClicked.isAvailable) {
                Toast.makeText(this, "Slot ${slotClicked.id} is already booked", Toast.LENGTH_SHORT).show()
                return@SlotAdapter
            }

            // Remove previous selection
            if (selectedIndex != -1 && selectedIndex < slots.size) {
                slots[selectedIndex].isSelected = false
                adapter.notifyItemChanged(selectedIndex)
            }

            // Set new selection
            slots[index].isSelected = true
            selectedIndex = index
            selectedSlot = slots[index]
            adapter.notifyItemChanged(index)

            btnBook.isEnabled = true
        }

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        btnBook.setOnClickListener {
            selectedSlot?.let { slot ->
                showLoadingAndProceed(slot)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_INDEX, selectedIndex)
    }

    // -----------------------
    // Loading Screen Function
    // -----------------------
    private fun showLoadingAndProceed(slot: ParkingSlot) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({

            val (finalPrice, _) = PricingUtil.computePrice(slot.basePrice.toDouble())

            val intent = Intent(this, BookingConfirmationActivity::class.java).apply {
                putExtra("slot_number", slot.id)
                putExtra("parking_name", parkingName)
                putExtra("price", finalPrice)
            }

            dialog.dismiss()
            startActivity(intent)
            finish()

        }, 2000) // 2-second loading screen
    }
}
