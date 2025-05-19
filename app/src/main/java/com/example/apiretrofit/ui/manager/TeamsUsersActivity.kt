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
import com.example.apiretrofit.adapter.TeamUserAdapter
import com.example.apiretrofit.api.model.UserTeam
import com.example.apiretrofit.api.services.ApiClient
import com.example.apiretrofit.api.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamsUsersActivity : AppCompatActivity() {
    private lateinit var swipeRefresh : SwipeRefreshLayout
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter : TeamUserAdapter
    private lateinit var api : ApiService
    private lateinit var listUserTeams : ArrayList<UserTeam>
    private var projectID: Int = -1
    private var teamID: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_teams_users)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        projectID = intent.getIntExtra("project_id", -1)
        teamID = intent.getIntExtra("team_id", -1)
        Log.d("intent", projectID.toString() + " " + teamID.toString())

        listUserTeams = arrayListOf()
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TeamUserAdapter(listUserTeams)
        recyclerView.adapter = adapter

        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            fetchTeams(projectID, teamID) // refresh saat swipe
        }

        api = ApiClient.getApiService(this)
        adapter = TeamUserAdapter(listUserTeams)
        recyclerView.adapter = adapter

        if (projectID != -1 && teamID != -1) {
            fetchTeams(projectID, teamID)
        } else {
            Toast.makeText(this, "ID tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchTeams(projectID : Int, teamID : Int){
        swipeRefresh.isRefreshing = true
        api.getTeamsUsers(projectID).enqueue(object : Callback<List<UserTeam>> {
            override fun onResponse(call: Call<List<UserTeam>>, response: Response<List<UserTeam>>) {
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful) {
                    listUserTeams.clear()
                    response.body()?.let { allTeams ->
                        val filteredTeams = allTeams.filter { it.id == teamID }
                        listUserTeams.addAll(filteredTeams)
                    }
                    Log.d("API_RESPONSE", listUserTeams.toString())
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<UserTeam>>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@TeamsUsersActivity, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}