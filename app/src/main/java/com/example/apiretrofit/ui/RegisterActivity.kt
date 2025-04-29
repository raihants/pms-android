package com.example.apiretrofit.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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

    private val roles = listOf("Manager", "Developer", "Designer", "Tester")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        fullNameInput = findViewById(R.id.fullNameInput)
        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        roleSpinner = findViewById(R.id.roleSpinner)
        registerButton = findViewById(R.id.registerButton)

        roleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)

        registerButton.setOnClickListener {
            val namaLengkap = fullNameInput.text.toString()
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val role = roleSpinner.selectedItem.toString()

            registerUser(namaLengkap, username, password, role)
            // Simpan user ke database atau kirim ke server di sini
        }
    }

    private fun registerUser(namaLengkap: String, username: String, password: String, role: String) {
        lifecycleScope.launch {
            try {
                val api = ApiClient.getApiService(this@RegisterActivity)
                val response = api.register(
                    User(
                        id = 0, // ID dummy
                        username = username,
                        password = password,
                        nama_lengkap = namaLengkap,
                        role = role
                    )
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registrasi berhasil. Silakan login.", Toast.LENGTH_LONG).show()
                    LoginActivity().loginUser(username, password)
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Registrasi gagal: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
