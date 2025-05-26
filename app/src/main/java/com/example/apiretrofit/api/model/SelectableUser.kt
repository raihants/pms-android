package com.example.apiretrofit.api.model

data class SelectableUser(
    val user: User,
    var isSelected: Boolean = false
)
