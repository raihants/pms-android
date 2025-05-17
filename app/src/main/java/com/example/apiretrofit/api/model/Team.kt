package com.example.apiretrofit.api.model

import java.lang.reflect.Member

data class Team (
    val id: Int,
    val name: String,
    val description: String,
    val project_id: Int,
)