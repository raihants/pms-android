package com.example.apiretrofit.ui

import android.content.Intent
import android.os.Bundle
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
import com.example.apiretrofit.ui.designer.DesignerMainActivity
import com.example.apiretrofit.ui.developer.DeveloperMainActivity
import com.example.apiretrofit.ui.manager.ManagerMainActivity
import com.example.apiretrofit.ui.tester.TesterMainActivity
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

                    val userRole = sessionManager.getUserRole()

                    if (userRole == "Manajer") {
                        // Kalau user developer
                        startActivity(Intent(this@LoginActivity, ManagerMainActivity::class.java))
                        Toast.makeText(this@LoginActivity, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else if (userRole == "developer") {
                        // Kalau user admin
                        startActivity(Intent(this@LoginActivity, DeveloperMainActivity::class.java))
                        Toast.makeText(this@LoginActivity, "Welcome Developer!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else if (userRole == "designer") {
                        // Kalau role lain
                        startActivity(Intent(this@LoginActivity, DesignerMainActivity::class.java))
                        Toast.makeText(this@LoginActivity, "Welcome Designer!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        startActivity(Intent(this@LoginActivity, TesterMainActivity::class.java))
                        Toast.makeText(this@LoginActivity, "Welcome Tester!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login gagal", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
