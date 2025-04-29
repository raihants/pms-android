package com.example.apiretrofit.api.services

import com.example.apiretrofit.api.model.LoginRequest
import com.example.apiretrofit.api.model.LoginResponse
import com.example.apiretrofit.api.model.RegisterResponse
import com.example.apiretrofit.api.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body request: User): Response<RegisterResponse>
}