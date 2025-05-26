package com.example.apiretrofit.api.model
import com.google.gson.annotations.SerializedName

data class TaskResponse(

    @SerializedName("project_id")
    val projectId: Int,

    @SerializedName("id")
    val taskID: Int,

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

    @SerializedName("user_id")
    val userID: Int,

    @SerializedName("user_name")
    val userName: String,
)

