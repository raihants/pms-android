package com.example.apiretrofit.api.services

import android.content.Context
import com.example.apiretrofit.api.session.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://pms-api-node-production.up.railway.app/api/"

    fun getApiService(context: Context): ApiService {
        val sessionManager = SessionManager(context)
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}