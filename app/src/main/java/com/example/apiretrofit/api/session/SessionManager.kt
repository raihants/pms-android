package com.example.apiretrofit.api.session

import android.content.Context
import android.widget.Toast
import com.example.apiretrofit.api.model.User

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        val currentTime = System.currentTimeMillis()
        prefs.edit()
            .putString("AUTH_TOKEN", token)
            .putLong("LOGIN_TIME", currentTime)
            .apply()
    }

    fun saveUserData(user: User) {
        prefs.edit()
            .putInt("USER_ID", user.id)
            .putString("USERNAME", user.username)
            .putString("NAMA_LENGKAP", user.nama_lengkap)
            .putString("ROLE", user.role)
            .apply()
    }

    fun fetchAuthToken(): String? = prefs.getString("AUTH_TOKEN", null)

    fun isTokenExpired(): Boolean {
        val loginTime = prefs.getLong("LOGIN_TIME", 0L)
        val currentTime = System.currentTimeMillis()
        val twoHoursMillis = 2 * 60 * 60 * 1000 // 2 jam dalam milidetik

        return (currentTime - loginTime) > twoHoursMillis
    }

    fun getValidToken(): String? {
        return if (isTokenExpired()) {
            clearSession()
            null
        } else {
            fetchAuthToken()
        }
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun getUserRole(): String? = prefs.getString("ROLE", null)

    fun isUserRole(role: String): Boolean {
        return getUserRole() == role
    }
}
