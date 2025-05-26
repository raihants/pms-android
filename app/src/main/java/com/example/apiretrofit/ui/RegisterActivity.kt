package com.example.apiretrofit.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.apiretrofit.R
import com.example.apiretrofit.api.model.LoginRequest
import com.example.apiretrofit.api.model.User
import com.example.apiretrofit.api.services.ApiClient
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var fullNameInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var registerButton: Button
    private lateinit var loginButton: TextView
    private lateinit var emailInput: EditText

    private val roles = listOf("Manajer", "Developer", "Designer", "Tester")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        emailInput = findViewById(R.id.emailInput)
        fullNameInput = findViewById(R.id.fullNameInput)
        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        roleSpinner = findViewById(R.id.roleSpinner)
        registerButton = findViewById(R.id.registerButton)
        loginButton = findViewById(R.id.loginButton)

        roleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)

        registerButton.setOnClickListener {
            val email = emailInput.text.toString()
            val namaLengkap = fullNameInput.text.toString()
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val role = roleSpinner.selectedItem.toString()

            registerUser(email, namaLengkap, username, password, role)
            // Simpan user ke database atau kirim ke server di sini
        }

        loginButton.setOnClickListener {
           startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }
    }

    private fun registerUser(email: String, namaLengkap: String ,username: String, password: String, role: String) {
        lifecycleScope.launch {
            try {
                val api = ApiClient.getApiService(this@RegisterActivity)
                val response = api.register(
                    User(
                        id = 0, // ID dumm
                        username = username,
                        password = password,
                        email = email,
                        name = namaLengkap,
                        role = role
                    )
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registrasi berhasil. Silakan login.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    Log.d("API", "Registrasi berhasil: ${response.body()}")
                    Log.d("API", response.toString())
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Registrasi gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("RegisterActivity", "Registrasi gagal: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
