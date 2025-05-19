package com.example.apiretrofit.api.model

data class LogResponse(
    val id: Int,
    val task_id: Int,
    val user_id: Int,
    val activity: String,
    val activity_time: String,
    val user_name: String,
    val task_name: String
)
