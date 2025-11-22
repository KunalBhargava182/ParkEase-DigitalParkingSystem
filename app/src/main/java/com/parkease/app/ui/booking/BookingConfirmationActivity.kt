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
import com.parkease.app.R
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

        // 1. Read intent extras
        val parkingName = intent.getStringExtra("parking_name") ?: "Unknown Parking"
        val slotNumber = intent.getIntExtra("slot_number", -1)
        val price = intent.getIntExtra("price", -1)
        val reference = intent.getStringExtra("reference") ?: generateReference()
        val bookingTime = intent.getStringExtra("booking_time") ?: currentTimestamp()

        // 2. Find views
        tvTitle = findViewById(R.id.tv_title)
        tvMessage = findViewById(R.id.tv_message)
        tvInfo = findViewById(R.id.tv_info)
        imgQr = findViewById(R.id.img_qr)
        btnOk = findViewById(R.id.btn_ok)
        btnViewDetails = findViewById(R.id.btn_view_details)

        // 3. Set UI Text
        tvTitle.text = "Booking Confirmed!"
        tvMessage.text = "You have selected Slot $slotNumber in $parkingName."

        // Format price text cleanly
        val priceText = if (price > 0) "• ₹$price" else ""
        tvInfo.text = "Reference: $reference • $bookingTime $priceText"

        // 4. Generate QR Content (Plain Text, NOT JSON)
        // This ensures the scanner shows readable text immediately
        val qrContent = """
            🅿️ PARKING TICKET
            ------------------
            Ref ID  : $reference
            Location: $parkingName
            Slot No : $slotNumber
            Amount  : ₹$price
            Date    : $bookingTime
            ------------------
            Verifiable via ParkEase
        """.trimIndent()

        // 5. Generate and Show QR Bitmap
        val qrBitmap = generateQrBitmap(qrContent, 512)
        if (qrBitmap != null) {
            imgQr.visibility = View.VISIBLE
            imgQr.setImageBitmap(qrBitmap)
        } else {
            imgQr.visibility = View.GONE
        }

        // 6. OK Button -> Go to Invoice/Payment
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

        // 7. Share Button -> Send nice text message
        btnViewDetails.setOnClickListener {
            val shareMessage = """
                   ParkEase Booking Confirmed!
                
                   Location: $parkingName
                   Slot: $slotNumber
                   Price: ₹$price
                   Reference: $reference
                   Time: $bookingTime
                
                Please show this at the entry.
            """.trimIndent()

            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_SUBJECT, "ParkEase Booking: $reference")
            share.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(share, "Share Booking Details"))
        }
    }

    // --- Helper Functions ---

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