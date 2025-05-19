package com.example.apiretrofit.ui.manager

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apiretrofit.R
import com.example.apiretrofit.adapter.TeamAdapter
import com.example.apiretrofit.adapter.TeamUserAdapter
import com.example.apiretrofit.api.model.Team
import com.example.apiretrofit.api.model.UserTeam
import com.example.apiretrofit.api.services.ApiClient
import com.example.apiretrofit.api.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamActivity : AppCompatActivity() {
    private lateinit var swipeRefresh : SwipeRefreshLayout
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : TeamAdapter
    private lateinit var api : ApiService
    private lateinit var listTeams : ArrayList<Team>
    private var projectID: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_team)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        projectID = intent.getIntExtra("project_id", -1)

        listTeams = arrayListOf()
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TeamAdapter(listTeams, this)
        recyclerView.adapter = adapter

        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            fetchTeams(projectID) // refresh saat swipe
        }

        api = ApiClient.getApiService(this)
        adapter = TeamAdapter(listTeams, this)
        recyclerView.adapter = adapter

        if (projectID != -1) {
            fetchTeams(projectID)
        } else {
            Toast.makeText(this, "ID proyek tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchTeams(projectID : Int){
        swipeRefresh.isRefreshing = true
        api.getTeams(projectID).enqueue(object : Callback<List<Team>> {
            override fun onResponse(call: Call<List<Team>>, response: Response<List<Team>>){
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful) {
                    listTeams.clear()
                    response.body()?.let { listTeams.addAll(it) }
                    Log.d("API_RESPONSE", listTeams.toString())
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onFailure(call: Call<List<Team>>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@TeamActivity, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}