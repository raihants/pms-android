package com.example.apiretrofit.ui.share

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apiretrofit.R
import com.example.apiretrofit.adapter.TeamAdapter
import com.example.apiretrofit.api.model.Team
import com.example.apiretrofit.api.services.ApiClient
import com.example.apiretrofit.api.services.ApiService
import com.example.apiretrofit.api.session.SessionManager
import com.example.apiretrofit.ui.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamActivity : AppCompatActivity() {
    private lateinit var swipeRefresh : SwipeRefreshLayout
    private lateinit var recyclerView : RecyclerView
    private lateinit var btnAdd : FloatingActionButton
    private lateinit var adapter : TeamAdapter
    private lateinit var api : ApiService
    private lateinit var listTeams : ArrayList<Team>
    private var projectID: Int = -1
    private lateinit var sessionManager: SessionManager

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

        sessionManager = SessionManager(this)

        listTeams = arrayListOf()
        recyclerView = findViewById(R.id.recyclerView)
        btnAdd = findViewById(R.id.btnAdd)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TeamAdapter(listTeams, this)
        recyclerView.adapter = adapter

        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            fetchTeams(projectID) // refresh saat swipe
        }

        btnAdd.setOnClickListener {
            showTeamDialog()
        }

        val role = sessionManager.isUserRole("Manajer")

        btnAdd.isVisible = role

        api = ApiClient.getApiService(this)
        adapter = TeamAdapter(listTeams, this)
        recyclerView.adapter = adapter

        if (projectID != -1) {
            fetchTeams(projectID)
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

    private fun createTeams(team: Team) {
        lifecycleScope.launch {
            try {
                val response = api.createTeam(team)
                if (response.isSuccessful) {
                    Toast.makeText(this@TeamActivity, "Team berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    Log.d("API", "${team} , ${response.body()}")
                    fetchTeams(projectID)
                } else {
                    Toast.makeText(this@TeamActivity, "Gagal menambahkan team", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TeamActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showTeamDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_team, null)
        val editName = dialogView.findViewById<EditText>(R.id.editName)
        val editDescription = dialogView.findViewById<EditText>(R.id.editDescription)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Tambah Tim")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val name = editName.text.toString()
                val description = editDescription.text.toString()

                if (name.isBlank() || description.isBlank()) {
                    Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newTeam = Team(
                    id = 0, // atau biarkan backend assign ID
                    name = name,
                    description = description,
                    project_id = projectID // pastikan ini tersedia dalam konteksmu
                )

                createTeams(newTeam)
            }
            .setNegativeButton("Batal", null)
            .create()

        dialog.show()
    }

}