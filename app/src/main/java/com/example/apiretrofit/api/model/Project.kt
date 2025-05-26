package com.example.apiretrofit.api.model

data class Project(
    val id: Int,
    val name: String,
    val description: String,
    val start_date: String,
    val end_date: String,
    val status: String,
    val budget: String,
    val manager_id: Int
)

