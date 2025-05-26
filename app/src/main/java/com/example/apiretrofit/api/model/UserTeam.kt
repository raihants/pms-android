package com.example.apiretrofit.api.model

import com.google.gson.annotations.SerializedName

data class UserTeam (
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("id")
    val id: Int,

    @SerializedName("user_name")
    val userName: String,

    @SerializedName("name")
    val teamName: String,

    @SerializedName("description")
    val teamDescription: String
)