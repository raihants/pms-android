package com.example.apiretrofit.api.model

import com.google.gson.annotations.SerializedName

data class ProjectReportResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("project_id") val projectId: Int,
    @SerializedName("team_id") val teamId: Int,
    @SerializedName("total_tasks") val totalTasks: Int,
    @SerializedName("completed_tasks") val completedTasks: Int,
    @SerializedName("progress") val progress: String,
    @SerializedName("estimated_time_left") val estimatedTimeLeft: String,
    @SerializedName("team_name") val teamName: String
)
