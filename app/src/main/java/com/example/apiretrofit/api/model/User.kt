package com.example.apiretrofit.api.model

data class User(
    val id: Int,
    val username: String,
    val password: String? = null,
    val email: String,
    val name: String,
    val role: String,
    val team_id: Int? = null,
)
