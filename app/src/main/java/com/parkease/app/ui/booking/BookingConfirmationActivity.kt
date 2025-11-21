package com.parkease.app.ui.booking

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.parkease.app.MainActivity
import com.parkease.app.R
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class BookingConfirmationActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvMessage: TextView
    private lateinit var tvInfo: TextView
    private lateinit var imgQr: ImageView
    private lateinit var btnOk: Button
    private lateinit var btnViewDetails: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirmation)

        // Read intent extras
        val parkingName = intent.getStringExtra("parking_name") ?: "Unknown Parking"
        val slotNumber = intent.getIntExtra("slot_number", -1)
        val price = intent.getIntExtra("price", -1)  // NOW using Int not Double
        val reference = intent.getStringExtra("reference") ?: generateReference()
        val bookingTime = intent.getStringExtra("booking_time") ?: currentTimestamp()

        // find views
        tvTitle = findViewById(R.id.tv_title)
        tvMessage = findViewById(R.id.tv_message)
        tvInfo = findViewById(R.id.tv_info)
        imgQr = findViewById(R.id.img_qr)
        btnOk = findViewById(R.id.btn_ok)
        btnViewDetails = findViewById(R.id.btn_view_details)

        // Main title
        tvTitle.text = "Booking Confirmed!"

        // Slot + parking name
        tvMessage.text = "You have selected Slot $slotNumber in $parkingName."

        // Clean price text
        val priceText = if (price > 0) "• ₹$price" else ""

        // Visible info text
        tvInfo.text = "Reference: $reference • $bookingTime $priceText"

        // QR JSON content
        val qrContent = JSONObject().apply {
            put("parking", parkingName)
            put("slot", slotNumber)
            put("price", "₹$price")      // CLEAN PRICE IN QR
            put("reference", reference)
            put("time", bookingTime)
            put("app", "ParkEase")
        }.toString()

        // Show QR
        val qrBitmap = generateQrBitmap(qrContent, 512)
        if (qrBitmap != null) {
            imgQr.visibility = View.VISIBLE
            imgQr.setImageBitmap(qrBitmap)
        } else {
            imgQr.visibility = View.GONE
        }

        // OK button
        btnOk.setOnClickListener {

            val intent = Intent(this, InvoiceActivity::class.java).apply {
                putExtra("parking", parkingName)
                putExtra("slot", slotNumber)
                putExtra("price", price)
                putExtra("reference", reference)
                putExtra("time", bookingTime)
            }

            startActivity(intent)
            finish()
        }



        // Share booking details
        btnViewDetails.setOnClickListener {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_SUBJECT, "Booking $reference")
            share.putExtra(Intent.EXTRA_TEXT, qrContent)
            startActivity(Intent.createChooser(share, "Share booking"))
        }
    }

    private fun generateQrBitmap(content: String, size: Int): Bitmap? {
        return try {
            val writer = MultiFormatWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
            val encoder = BarcodeEncoder()
            encoder.createBitmap(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun generateReference(): String {
        val rnd = (1000..9999).random()
        return "BKG-$rnd"
    }

    private fun currentTimestamp(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }
}
