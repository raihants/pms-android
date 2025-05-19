package com.example.apiretrofit.ui.manager

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
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
import com.example.apiretrofit.api.model.User
import com.example.apiretrofit.api.services.ApiClient
import com.example.apiretrofit.api.services.ApiService
import com.example.apiretrofit.api.session.SessionManager
import com.example.apiretrofit.ui.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ManagerMainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var projectList: ArrayList<Project>
    private lateinit var adapter: ProjectAdapter
    private lateinit var api: ApiService
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        btnAdd = findViewById(R.id.btnAdd)

        recyclerView.layoutManager = LinearLayoutManager(this)
        projectList = arrayListOf()
        adapter = ProjectAdapter(projectList, this)
        recyclerView.adapter = adapter

        sessionManager = SessionManager(this)
        api = ApiClient.getApiService(this)

        fetchProjects()

        swipeRefresh.setOnRefreshListener {
            fetchProjects()
        }

        btnAdd.setOnClickListener {
            showProjectDialog()
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
        api.getProjects().enqueue(object : Callback<List<Project>> {
            override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful) {
                    projectList.clear()
                    response.body()?.let { projectList.addAll(it) }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@ManagerMainActivity, "Gagal ambil data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    fun showProjectDialog(project: Project? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_project, null)
        val name = dialogView.findViewById<EditText>(R.id.editName)
        val description = dialogView.findViewById<EditText>(R.id.editDescription)
        val startDate = dialogView.findViewById<EditText>(R.id.editStartDate)
        val endDate = dialogView.findViewById<EditText>(R.id.editEndDate)
        val budget = dialogView.findViewById<EditText>(R.id.editBudget)
        val status = dialogView.findViewById<Spinner>(R.id.spinnerStatus)

        val statuses = arrayOf("Perencanaan", "Berjalan", "Selesai")
        val adapterStatus =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)
        status.adapter = adapterStatus

        val calendar = Calendar.getInstance()
        val datePickerListener = { view: EditText ->
            val dp = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    view.setText("$year-${month + 1}-$dayOfMonth")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dp.show()
        }

        startDate.setOnClickListener { datePickerListener(startDate) }
        endDate.setOnClickListener { datePickerListener(endDate) }

        if (project != null) {
            name.setText(project.name)
            description.setText(project.description)
            startDate.setText(project.start_date.takeIf { it.length >= 10 }?.substring(0, 10) ?: "")
            endDate.setText(project.end_date.takeIf { it.length >= 10 }?.substring(0, 10) ?: "")
            budget.setText(project.budget)
            val statusIndex = statuses.indexOfFirst { it.equals(project.status, ignoreCase = true) }
            if (statusIndex >= 0) {
                status.setSelection(statusIndex)
            }
        }

        val dialog = AlertDialog.Builder(this)
        .setTitle(if (project == null) "Tambah Project" else "Edit Project")
        .setView(dialogView)
        .setPositiveButton(if (project == null) "Simpan" else "Update") { _, _ ->
            val projectData = Project(
                id = project?.id ?: 0,
                name = name.text.toString(),
                description = description.text.toString(),
                start_date = startDate.text.toString(),
                end_date = endDate.text.toString(),
                status = status.selectedItem.toString(),
                budget = budget.text.toString(),
                manager_id = sessionManager.getUserId()
            )

            if (project == null) {
                createProject(projectData)
            } else {
                updateProject(projectData)
            }
        }

        .setNegativeButton("Batal", null)
        .create()
        dialog.show()

    }

    private fun createProject(project: Project) {
        lifecycleScope.launch {
            try {
                val response = api.createProject(project)
                if (response.isSuccessful) {
                    Toast.makeText(this@ManagerMainActivity, "Proyek berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    fetchProjects()
                } else {
                    Toast.makeText(this@ManagerMainActivity, "Gagal menambahkan proyek", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ManagerMainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateProject(project: Project) {
        api.updateProject(project.id, project).enqueue(object : Callback<Project> {
            override fun onResponse(call: Call<Project>, response: Response<Project>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ManagerMainActivity, "Proyek diperbarui", Toast.LENGTH_SHORT).show()
                    fetchProjects()
                } else {
                    Toast.makeText(this@ManagerMainActivity, "Gagal memperbarui proyek", Toast.LENGTH_SHORT).show()
                    Log.e("API", "Error updating project: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Project>, t: Throwable) {
                Toast.makeText(this@ManagerMainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun deleteProject(project: Project) {
            api.deleteProject(project.id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    fetchProjects()
                    Toast.makeText(this@ManagerMainActivity, "Proyek dihapus", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(
                        this@ManagerMainActivity,
                        "Gagal hapus proyek",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
