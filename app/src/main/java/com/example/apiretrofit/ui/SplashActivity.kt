package com.example.apiretrofit.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apiretrofit.api.session.SessionManager
import com.example.apiretrofit.ui.manager.ManagerMainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        if (sessionManager.getValidToken() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, "Sesi anda sudah habis, silahkan login kembali", Toast.LENGTH_SHORT).show()
        } else {
            startActivity(Intent(this, ManagerMainActivity::class.java))
        }
        finish()
    }
}