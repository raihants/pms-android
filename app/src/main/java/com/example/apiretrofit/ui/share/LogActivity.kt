package com.example.apiretrofit.ui.share

import android.content.Intent
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
import com.example.apiretrofit.adapter.LogAdapter
import com.example.apiretrofit.api.model.LogResponse
import com.example.apiretrofit.api.services.ApiClient
import com.example.apiretrofit.api.services.ApiService
import com.example.apiretrofit.api.session.SessionManager
import com.example.apiretrofit.ui.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var logList: ArrayList<LogResponse>
    private lateinit var adapter: LogAdapter
    private lateinit var api: ApiService
    private var projectID: Int = -1
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        projectID = intent.getIntExtra("project_id", -1)
        Log.d("project_id", projectID.toString())

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            fetchLogs(projectID) // refresh saat swipe
        }

        api = ApiClient.getApiService(this)
        logList = arrayListOf()
        adapter = LogAdapter(logList)
        recyclerView.adapter = adapter

        if (projectID != -1) {
            fetchLogs(projectID)
        } else {
            Toast.makeText(this, "ID proyek tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                Toast.makeText(this, "Klik Profile", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_logout -> {
                sessionManager.clearSession()
                Toast.makeText(this, "Anda telah logout", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchLogs(projectID : Int){
        swipeRefresh.isRefreshing = true
        api.getLogs(projectID).enqueue(object : Callback<List<LogResponse>> {
            override fun onResponse(call: Call<List<LogResponse>>, response: Response<List<LogResponse>>){
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful) {
                    logList.clear()
                    response.body()?.let { logList.addAll(it) }
                    Log.d("API_RESPONSE", logList.toString())
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onFailure(call: Call<List<LogResponse>>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@LogActivity, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
