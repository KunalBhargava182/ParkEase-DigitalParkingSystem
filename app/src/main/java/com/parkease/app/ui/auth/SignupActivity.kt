package com.parkease.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.parkease.app.MainActivity
import com.parkease.app.R
import com.parkease.app.auth.AuthPrefs

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If already logged in, skip
        if (AuthPrefs.isLoggedIn(this)) {
            startMainAndFinish()
            return
        }

        setContentView(R.layout.activity_signup)

        val etName = findViewById<EditText>(R.id.et_name)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnSignup = findViewById<Button>(R.id.btn_signup)

        btnSignup.setOnClickListener {
            // Dummy signup: accept anything
            val name = etName.text.toString().ifBlank { "Guest" }
            AuthPrefs.setLoggedIn(this, name)
            startMainAndFinish()
        }
    }

    private fun startMainAndFinish() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
