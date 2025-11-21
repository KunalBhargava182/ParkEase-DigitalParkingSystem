package com.parkease.app.ui.selection

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.parkease.app.R
import com.parkease.app.ui.details.AreaDetailsActivity

data class ParkingArea(
    val name: String,
    val address: String,
    val distance: String
)

class ParkingSelectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ParkingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_selection)

        recyclerView = findViewById(R.id.recycler_parking_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val parkingAreas = listOf(
            ParkingArea("Jain College Parking (Jayanagar)", "Near Jain College, 4th T Block, Jayanagar", "0.2 km away"),
            ParkingArea("Jayanagar 4th Block Multi-level", "4th Block, Jayanagar", "0.3 km away"),
            ParkingArea("Jayanagar Bus Stand Lot", "Near Jayanagar Bus Stand, 3rd T Block", "0.4 km away"),
            ParkingArea("7th Block Open Parking", "7th Block, Jayanagar", "0.6 km away"),
            ParkingArea("9th Block Mall Parking", "9th Block, Jayanagar (Shopping Complex)", "0.8 km away"),
            ParkingArea("South End Circle Parking", "South End Circle, Jayanagar", "1.0 km away"),
            ParkingArea("Padmanabha Theatre Parking", "Near Padmanabha Theatre, Jayanagar", "1.1 km away"),
            ParkingArea("Wilson Garden Street Parking", "Wilson Garden (near Jayanagar)", "1.3 km away"),
            ParkingArea("JP Nagar Link Road Lot", "JP Nagar 3rd Phase (near Jayanagar)", "1.6 km away"),
            ParkingArea("Banashankari Market Parking", "Banashankari 2nd Stage (close to Jayanagar)", "1.8 km away"),
            ParkingArea("Shivaji Nagar Public Parking", "Shivaji Nagar (short drive from Jayanagar)", "2.0 km away"),
            ParkingArea("Ragigudda Temple Parking", "Ragigudda, Jayanagar side", "2.2 km away"),
            ParkingArea("Eshwarappa Layout Parking", "Eshwarappa Layout, South Bangalore", "2.4 km away"),
            ParkingArea("Jayanagar Metro Station Lot", "Near Jayanagar Metro Station, 4th Block", "0.5 km away"),
            ParkingArea("Residency Road Overflow Parking", "Residency Road (towards Jayanagar)", "2.6 km away"),
            ParkingArea("SBRR Mahajana College Parking", "SBRR Mahajana College rd (nearby)", "2.8 km away"),
            ParkingArea("Medical Layout Parking", "Medical Layout / Jayanagar fringe", "3.0 km away"),
            ParkingArea("NGA Street Parking", "Near NGA street (close to Jayanagar)", "1.4 km away"),
            ParkingArea("Shanthi Nagar Public Lot", "Shanthi Nagar, Jayanagar area", "0.9 km away"),
            ParkingArea("Tech Park Visitor Parking", "Small tech park near Jayanagar (visitor slots)", "3.2 km away")
        )

        adapter = ParkingAdapter(parkingAreas) { selectedArea ->
            val intent = Intent(this, AreaDetailsActivity::class.java)
            intent.putExtra("parking_name", selectedArea.name)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
    }
}
