package com.example.apiretrofit.ui.tester

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apiretrofit.R
import com.example.apiretrofit.api.session.SessionManager
import com.example.apiretrofit.ui.LoginActivity

class TesterMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tester_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val sessionManager = SessionManager(this)
        val btnLogOut = findViewById<Button>(R.id.btnLogOut)
        btnLogOut.setOnClickListener {
            sessionManager.clearSession()
            Toast.makeText(this, "Anda telah logout", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}