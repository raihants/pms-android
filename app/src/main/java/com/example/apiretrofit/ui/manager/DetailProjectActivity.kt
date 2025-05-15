package com.example.apiretrofit.ui.manager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apiretrofit.R
import com.example.apiretrofit.adapter.ProjectAdapter
import com.example.apiretrofit.adapter.TaskAdapter
import com.example.apiretrofit.api.model.Project
import com.example.apiretrofit.api.model.Tasks
import com.example.apiretrofit.api.services.ApiClient
import com.example.apiretrofit.api.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class DetailProjectActivity : AppCompatActivity() {
    private lateinit var taskList: ArrayList<Tasks>
    private lateinit var progressBar: ProgressBar
    private lateinit var api: ApiService
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_project)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        taskList = ArrayList() // ✅ tambahkan ini

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(taskList) // inisialisasi adapter juga
        recyclerView.adapter = adapter

        api = ApiClient.getApiService(this)

        progressBar = findViewById(R.id.progressBar)

        val id = intent.getIntExtra("project_id", -1)
        Log.d("project_id", id.toString())
        if (id != -1) {
            fetchProjects(id)
        } else {
            Toast.makeText(this, "ID proyek tidak ditemukan", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun fetchProjects( id : Int) {
        progressBar.visibility = View.VISIBLE
        api.getTasks(id).enqueue(object : Callback<List<Tasks>> {
            override fun onResponse(call: Call<List<Tasks>>, response: Response<List<Tasks>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    taskList.clear()
                    response.body()?.let { taskList.addAll(it) }
                    Log.d("API_RESPONSE", taskList.toString())
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Tasks>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@DetailProjectActivity, "Gagal ambil data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}