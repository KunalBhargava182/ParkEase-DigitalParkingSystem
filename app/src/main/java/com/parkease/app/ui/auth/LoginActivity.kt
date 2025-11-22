package com.parkease.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.parkease.app.MainActivity
import com.parkease.app.R
import com.parkease.app.auth.AuthPrefs

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If already logged in, skip
        if (AuthPrefs.isLoggedIn(this)) {
            startMainAndFinish()
            return
        }

        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvSignup = findViewById<TextView>(R.id.tv_signup)

        btnLogin.setOnClickListener {
            // Dummy auth: accept anything
            val entered = etEmail.text.toString().ifBlank { "Guest" }
            AuthPrefs.setLoggedIn(this, entered)
            startMainAndFinish()
        }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun startMainAndFinish() {
        val intent = Intent(this, MainActivity::class.java)
        // Clear back stack so user can't return to login with back button
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
