package com.parkease.app.ui.loading

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.parkease.app.R
import com.parkease.app.ui.selection.ParkingSelectionActivity

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        // Simulate API or location processing
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, ParkingSelectionActivity::class.java)
            startActivity(intent)
            finish()
        }, 2500) // 2.5 seconds delay
    }
}
