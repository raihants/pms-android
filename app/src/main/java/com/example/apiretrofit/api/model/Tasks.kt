package com.example.apiretrofit.api.model
import com.google.gson.annotations.SerializedName

data class Tasks(
    @SerializedName("project_id")
    val projectId: Int,

    @SerializedName("name")
    val taskName: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("priority")
    val priority: String,

    @SerializedName("start_date")
    val startDate: String,

    @SerializedName("end_date")
    val endDate: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("user_name")
    val userName: String,
)

