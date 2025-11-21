package com.parkease.app.auth

import android.content.Context

object AuthPrefs {
    private const val PREFS = "parkease_auth"
    private const val KEY_LOGGED_IN = "is_logged_in"
    private const val KEY_USER = "user_name"

    fun setLoggedIn(context: Context, userName: String?) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        p.edit().putBoolean(KEY_LOGGED_IN, true)
            .putString(KEY_USER, userName ?: "")
            .apply()
    }

    fun setLoggedOut(context: Context) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        p.edit().putBoolean(KEY_LOGGED_IN, false)
            .putString(KEY_USER, "")
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return p.getBoolean(KEY_LOGGED_IN, false)
    }

    fun getUserName(context: Context): String {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return p.getString(KEY_USER, "") ?: ""
    }
}
