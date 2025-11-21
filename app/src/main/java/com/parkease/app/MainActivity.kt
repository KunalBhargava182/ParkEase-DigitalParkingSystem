package com.parkease.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.parkease.app.auth.AuthPrefs
import com.parkease.app.ui.auth.LoginActivity
import com.parkease.app.ui.loading.LoadingActivity

class MainActivity : AppCompatActivity() {

    private val TAG = "ParkEase"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    // Register permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d(TAG, "permissionLauncher callback, granted=$granted")
        if (granted) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            getLastLocationAndProceed()
        } else {
            Toast.makeText(
                this,
                "Location permission is required to find nearby parking slots.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Redirect to Login if user is not logged in
        if (!AuthPrefs.isLoggedIn(this)) {
            val li = Intent(this, LoginActivity::class.java)
            li.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(li)
            finish()
            return
        }

        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Use Play Services LocationRequest
        locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            numUpdates = 1
            interval = 0L
            fastestInterval = 0L
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation
                Log.d(TAG, "locationCallback.onLocationResult: $loc")
                if (loc != null) {
                    onLocationAcquired(loc)
                } else {
                    Log.w(TAG, "locationCallback result but location is null")
                    Toast.makeText(this@MainActivity, "Got null location in callback", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onLocationAvailability(av: LocationAvailability) {
                super.onLocationAvailability(av)
                Log.d(TAG, "location availability: ${av.isLocationAvailable}")
            }
        }

        findViewById<Button>(R.id.btn_allow)?.setOnClickListener {
            requestLocationPermission()
        }

        // Auto-fetch if already granted
        val has = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        Log.d(TAG, "initial permission granted? $has")
        if (has) {
            getLastLocationAndProceed()
        }
    }

    private fun requestLocationPermission() {
        Log.d(TAG, "requestLocationPermission()")
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun isLocationEnabled(): Boolean {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return try {
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
            Log.e(TAG, "isLocationEnabled check failed", ex)
            false
        }
    }

    private fun getLastLocationAndProceed() {
        Log.d(TAG, "getLastLocationAndProceed() called")

        // Check device location (GPS) is enabled
        if (!isLocationEnabled()) {
            Toast.makeText(this, "Please enable Location (GPS) on your device", Toast.LENGTH_LONG).show()
            Log.w(TAG, "Device location disabled")
            return
        }

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    Log.d(TAG, "fusedLocationClient.lastLocation success: $location")
                    if (location != null) {
                        onLocationAcquired(location)
                    } else {
                        // last location null — request a fresh single update
                        Toast.makeText(this, "Getting fresh location...", Toast.LENGTH_SHORT).show()
                        try {
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.getMainLooper()
                            )
                            Log.d(TAG, "requestLocationUpdates() called")
                        } catch (ex: SecurityException) {
                            Log.e(TAG, "SecurityException while requesting updates", ex)
                        } catch (ex: Exception) {
                            Log.e(TAG, "Exception while requesting updates", ex)
                            Toast.makeText(this, "Failed to request location update: ${ex.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "lastLocation failed", ex)
                    Toast.makeText(this, "Unable to fetch location: ${ex.message}", Toast.LENGTH_LONG).show()
                }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException on lastLocation", e)
            Toast.makeText(this, "Permission missing: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun onLocationAcquired(location: Location) {
        Log.d(TAG, "onLocationAcquired: $location")
        val lat = location.latitude
        val lng = location.longitude
        Toast.makeText(this, "Location: $lat, $lng", Toast.LENGTH_SHORT).show()

        // Stop further updates (if any)
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (ex: Exception) {
            Log.w(TAG, "removeLocationUpdates failed: ${ex.message}")
        }

        // Navigate to LoadingActivity
        val intent = Intent(this, LoadingActivity::class.java).apply {
            putExtra("user_lat", lat)
            putExtra("user_lng", lng)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (_: Exception) {}
    }
}
