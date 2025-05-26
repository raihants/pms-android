package com.example.apiretrofit.ui.others

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apiretrofit.R
import com.example.apiretrofit.adapter.ProjectAdapter
import com.example.apiretrofit.api.model.Project
import com.example.apiretrofit.api.model.ProjectOthers
import com.example.apiretrofit.api.services.ApiClient
import com.example.apiretrofit.api.services.ApiService
import com.example.apiretrofit.api.session.SessionManager
import com.example.apiretrofit.ui.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import java.util.Calendar

class OthersMainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var projectList: MutableList<Project>
    private lateinit var adapter: ProjectAdapter
    private lateinit var api: ApiService
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_others_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recyclerView)
        swipeRefresh = findViewById(R.id.swipeRefresh)

        recyclerView.layoutManager = LinearLayoutManager(this)
        projectList = arrayListOf()

        sessionManager = SessionManager(this)
        val role = sessionManager.isUserRole("Manajer")

        adapter = ProjectAdapter(projectList, this, role,
            onEditClicked = { project ->
                showProjectDialog(project)
            },
            onDeleteClicked = { project ->
            })
        recyclerView.adapter = adapter

        api = ApiClient.getApiService(this)

        fetchProjects()

        swipeRefresh.setOnRefreshListener {
            fetchProjects()
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


    private fun fetchProjects() {
        swipeRefresh.isRefreshing = true
        val userID = sessionManager.getUserId()
        api.getProjectsUsers(userID).enqueue(object : Callback<Project> {
            override fun onResponse(call: Call<Project>, response: Response<Project>) {
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful) {
                    val allProjects = response.body()
                    Log.d("DEBUG", "Semua project dari API: $allProjects")
                    Log.d("USERID", "${userID}")
                    projectList.clear()
                    allProjects?.let { projectList.add(it) }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<Project>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Log.e("ERROR_FAILURE", "Throwable: ${t.message}", t)
                Toast.makeText(this@OthersMainActivity, "Gagal ambil data (jaringan/error lainnya)", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun showProjectDialog(project: Project) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_project, null)
        val name = dialogView.findViewById<EditText>(R.id.editName)
        val description = dialogView.findViewById<EditText>(R.id.editDescription)
        val startDate = dialogView.findViewById<EditText>(R.id.editStartDate)
        val endDate = dialogView.findViewById<EditText>(R.id.editEndDate)
        val budget = dialogView.findViewById<EditText>(R.id.editBudget)
        val status = dialogView.findViewById<Spinner>(R.id.spinnerStatus)

        val statuses = arrayOf("Perencanaan", "Berjalan", "Selesai")
        val adapterStatus = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)
        status.adapter = adapterStatus

        // Set data lama
        name.setText(project.name)
        description.setText(project.description)
        startDate.setText(project.start_date.takeIf { it.length >= 10 }?.substring(0, 10) ?: "")
        endDate.setText(project.end_date.takeIf { it.length >= 10 }?.substring(0, 10) ?: "")
        budget.setText(project.budget)
        val statusIndex = statuses.indexOfFirst { it.equals(project.status, ignoreCase = true) }
        if (statusIndex >= 0) {
            status.setSelection(statusIndex)
        }

        // Disable semua kecuali status
        name.isEnabled = false
        description.isEnabled = false
        startDate.isEnabled = false
        status.isEnabled = false
        endDate.isEnabled = false
        budget.isEnabled = false

        val dialog = AlertDialog.Builder(this)
            .setTitle("Detail Project")
            .setView(dialogView)
            .setNegativeButton("Tutup", null)
            .create()
        dialog.show()
    }

}