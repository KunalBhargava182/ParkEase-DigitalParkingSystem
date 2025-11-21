package com.parkease.app.ui.booking

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.parkease.app.R

class InvoiceActivity : AppCompatActivity() {

    private lateinit var tvParking: TextView
    private lateinit var tvSlot: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvRef: TextView
    private lateinit var tvTime: TextView
    private lateinit var imgQr: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        tvParking = findViewById(R.id.tv_parking)
        tvSlot = findViewById(R.id.tv_slot)
        tvPrice = findViewById(R.id.tv_price)
        tvRef = findViewById(R.id.tv_reference)
        tvTime = findViewById(R.id.tv_time)
        imgQr = findViewById(R.id.img_qr_invoice)

        // Data received from previous screen
        val parking = intent.getStringExtra("parking") ?: ""
        val slot = intent.getIntExtra("slot", -1)
        val price = intent.getIntExtra("price", 0)
        val reference = intent.getStringExtra("reference") ?: ""
        val time = intent.getStringExtra("time") ?: ""

        // Set UI fields
        tvParking.text = parking
        tvSlot.text = "Slot: $slot"
        tvPrice.text = "Price: ₹$price"
        tvRef.text = "Reference: $reference"
        tvTime.text = "Time: $time"

        // -------------------------------
// CREATE UPI QR CODE (ONLY PRICE)
// -------------------------------
        val upiString = """
    upi://pay?pa=yourupi@bank
    &pn=Parking+Payment
    &tn=Parking+Slot+Payment
    &am=$price
    &cu=INR
""".trimIndent().replace("\n", "")

        val bitmap = generateQrBitmap(upiString, 650)
        imgQr.setImageBitmap(bitmap)

    }

    private fun generateQrBitmap(content: String, size: Int): Bitmap? {
        return try {
            val writer = MultiFormatWriter()
            val matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
            val encoder = BarcodeEncoder()
            encoder.createBitmap(matrix)
        } catch (e: Exception) {
            null
        }
    }
}
