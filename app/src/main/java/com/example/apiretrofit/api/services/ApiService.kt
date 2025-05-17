package com.example.apiretrofit.api.services

import com.example.apiretrofit.api.model.LoginRequest
import com.example.apiretrofit.api.model.LoginResponse
import com.example.apiretrofit.api.model.Project
import com.example.apiretrofit.api.model.ProjectResponse
import com.example.apiretrofit.api.model.RegisterResponse
import com.example.apiretrofit.api.model.TaskRequest
import com.example.apiretrofit.api.model.User
import com.example.apiretrofit.api.model.TaskResponse
import com.example.apiretrofit.api.model.UserByProjectID
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.Call


interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body request: User): Response<RegisterResponse>

    @GET("projects")
    fun getProjects(): Call<List<Project>>

    @GET("teams/{id}")
    fun getUserByProjID(@Path("id") id: Int): Call<List<UserByProjectID>>

    @POST("projects")
    suspend fun createProject(@Body request: Project): Response<ProjectResponse>

    @PUT("projects/{id}")
    fun updateProject(@Path("id") id: Int, @Body project: Project): Call<Project>

    @DELETE("projects/{id}")
    fun deleteProject(@Path("id") id: Int): Call<Void>

    @GET("tasks/{id}")
    fun getTasks(@Path("id") id: Int): Call<List<TaskResponse>>

    @POST("tasks")
    suspend fun createTask(@Body request: TaskRequest): Response<TaskResponse>

    @PUT("tasks/{id}")
    fun updateTask(@Path("id") id: Int, @Body task: TaskRequest): Call<TaskResponse>

    @DELETE("tasks/{id}")
    fun deleteTask(@Path("id") id: Int): Call<Void>

}

