package com.example.apiretrofit.ui.share

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apiretrofit.R
import com.example.apiretrofit.adapter.TeamUserAdapter
import com.example.apiretrofit.adapter.UserSelectionAdapter
import com.example.apiretrofit.api.model.SelectableUser
import com.example.apiretrofit.api.model.User
import com.example.apiretrofit.api.model.UserTeam
import com.example.apiretrofit.api.services.ApiClient
import com.example.apiretrofit.api.services.ApiService
import com.example.apiretrofit.api.session.SessionManager
import com.example.apiretrofit.ui.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamsUsersActivity : AppCompatActivity() {
    private lateinit var swipeRefresh : SwipeRefreshLayout
    private lateinit var recyclerView : RecyclerView
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var adapter : TeamUserAdapter
    private lateinit var api : ApiService
    private lateinit var listUserTeams : ArrayList<UserTeam>
    private lateinit var sessionManager: SessionManager

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

        sessionManager = SessionManager(this)

        listUserTeams = arrayListOf()
        btnAdd = findViewById(R.id.btnAdd)
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

        btnAdd.setOnClickListener{
            fetchUsersForDialog()
        }

        val role = sessionManager.isUserRole("Manajer")

        btnAdd.isVisible = role
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

    private fun fetchTeams(projectID: Int, teamID: Int) {
        swipeRefresh.isRefreshing = true
        api.getTeamsUsers(projectID).enqueue(object : Callback<List<UserTeam>> {
            override fun onResponse(call: Call<List<UserTeam>>, response: Response<List<UserTeam>>) {
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful) {
                    listUserTeams.clear()
                    response.body()?.let { allTeams ->
                        val filteredTeams = allTeams.filter { it.id == teamID }
                        if (filteredTeams.isEmpty()) {
                            Toast.makeText(this@TeamsUsersActivity, "Tidak ada user", Toast.LENGTH_SHORT).show()
                            Log.d("API_RESPONSE", "Filtered team kosong")
                        } else {
                            listUserTeams.addAll(filteredTeams)
                            Log.d("API_RESPONSE", listUserTeams.toString())
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@TeamsUsersActivity, "Gagal ambil data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UserTeam>>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@TeamsUsersActivity, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun fetchUsersForDialog() {
        api.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val availableUsers = response.body()?.filter { it.team_id == null } ?: emptyList()
                    if (availableUsers.isEmpty()){
                        Toast.makeText(this@TeamsUsersActivity, "Tidak ada user tersedia", Toast.LENGTH_SHORT).show()
                        return
                    } else {
                        showUserSelectionDialog(availableUsers) { selectedUsers ->
                            var counter = selectedUsers.size
                            if (counter == 0) {
                                Toast.makeText(this@TeamsUsersActivity, "Tidak ada user dipilih", Toast.LENGTH_SHORT).show()
                                return@showUserSelectionDialog
                            }

                            selectedUsers.forEach { user ->
                                updateUserTeam(user, teamID) {
                                    counter--
                                    if (counter == 0) {
                                        Toast.makeText(this@TeamsUsersActivity, "Berhasil menambahkan user", Toast.LENGTH_SHORT).show()
                                        fetchTeams(projectID, teamID)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(this@TeamsUsersActivity, "Gagal ambil data user", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUserTeam(user: User, newTeamId: Int, onComplete: () -> Unit) {
        val updatedUser = user.copy(team_id = newTeamId)

        // Log data sebelum dikirim
        Log.d("UPDATE_USER", "Mengirim data update: $updatedUser")

        api.updateUser(user.id, updatedUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Log.d("UPDATE_USER", "Berhasil update user: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("UPDATE_USER", "Gagal update user: code ${response.code()}, error: $errorBody")
                }
                onComplete()
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("UPDATE_USER", "Error: ${t.message}", t)
                onComplete()
            }
        })
    }


    private fun showUserSelectionDialog(users: List<User>, onUsersSelected: (List<User>) -> Unit) {
        val selectableUsers = users.map { SelectableUser(it) }.toMutableList()

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_selection, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = UserSelectionAdapter(selectableUsers)
        recyclerView.adapter = adapter

        AlertDialog.Builder(this)
            .setTitle("Pilih User untuk Team")
            .setView(dialogView)
            .setPositiveButton("Tambahkan") { _, _ ->
                val selectedUsers = selectableUsers.filter { it.isSelected }.map { it.user }
                onUsersSelected(selectedUsers)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

}