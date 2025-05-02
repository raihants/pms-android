package com.example.apiretrofit.api.model

data class Project(
    val id: Int,
    val Name: String,
    val description: String,
    val start_date: String,
    val end_date: String,
    val status: Int,
    val budget: Int,
    val manager_id: Int
)

