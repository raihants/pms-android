package com.example.apiretrofit.api.model

data class Project(
    val id: Int,
    val nama_project: String,
    val deskripsi: String,
    val tanggal_mulai: String,
    val tanggal_selesai: String,
    val status: String
)
