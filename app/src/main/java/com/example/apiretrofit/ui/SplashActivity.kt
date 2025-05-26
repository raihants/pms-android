package com.example.apiretrofit.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apiretrofit.api.session.SessionManager
import com.example.apiretrofit.ui.manager.ManagerMainActivity
import com.example.apiretrofit.ui.others.OthersMainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)
        val token = sessionManager.getValidToken()
        val role = sessionManager.getUserRole()

        if (token == null) {
            Toast.makeText(this, "Sesi anda sudah habis, silahkan login kembali", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            if (role == "Manajer") {
                startActivity(Intent(this, ManagerMainActivity::class.java))
            } else if (role == "Developer" || role == "Tester" || role == "Designer") {
                startActivity(Intent(this, OthersMainActivity::class.java))
            } else {
                Toast.makeText(this, "Role tidak dikenali, silakan login kembali", Toast.LENGTH_SHORT).show()
                sessionManager.clearSession()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        finish()
    }
}
