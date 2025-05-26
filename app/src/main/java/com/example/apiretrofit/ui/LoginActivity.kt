package com.example.apiretrofit.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.apiretrofit.R
import com.example.apiretrofit.api.model.LoginRequest
import com.example.apiretrofit.api.services.ApiClient
import com.example.apiretrofit.api.session.SessionManager
import com.example.apiretrofit.ui.manager.ManagerMainActivity
import com.example.apiretrofit.ui.others.OthersMainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            val username = findViewById<EditText>(R.id.usernameInput).text.toString()
            val password = findViewById<EditText>(R.id.passwordInput).text.toString()
            loginUser(username, password)
        }

        findViewById<TextView>(R.id.registerButton).setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()
        }
    }

    fun loginUser(username: String, password: String) {
        lifecycleScope.launch {
            try {
                val api = ApiClient.getApiService(this@LoginActivity)
                val response = api.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val body = response.body()!!
                    sessionManager.saveAuthToken(body.token)
                    sessionManager.saveUserData(body.user)

                    val userRole = sessionManager.isUserRole("Manajer")

                    if (userRole) {
                        startActivity(Intent(this@LoginActivity, ManagerMainActivity::class.java))
                        Toast.makeText(this@LoginActivity, "Welcome Manager!", Toast.LENGTH_SHORT).show()
                    } else {
                        startActivity(Intent(this@LoginActivity, OthersMainActivity::class.java))
                        Toast.makeText(this@LoginActivity, "Welcome Developer!", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this@LoginActivity, "Login gagal", Toast.LENGTH_SHORT).show()
                    Log.e("API", "Error login: ${response.errorBody()?.string()}")
                    Log.e("API", LoginRequest(username, password).toString())
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
