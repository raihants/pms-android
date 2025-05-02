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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apiretrofit.R
import com.example.apiretrofit.adapter.ProjectAdapter
import com.example.apiretrofit.api.model.Project
import com.example.apiretrofit.api.model.User
import com.example.apiretrofit.api.services.ApiClient
import com.example.apiretrofit.api.services.ApiService
import com.example.apiretrofit.api.session.SessionManager
import com.example.apiretrofit.ui.LoginActivity
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ManagerMainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAdd: Button
    private lateinit var btnLogout: Button
    private lateinit var tvErr: TextView
    private lateinit var tvErr1: TextView
    private lateinit var projectList: ArrayList<Project>
    private lateinit var adapter: ProjectAdapter
    private lateinit var api: ApiService
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_main)

        tvErr = findViewById(R.id.txtError)
        tvErr1 = findViewById(R.id.txtError1)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        btnAdd = findViewById(R.id.btnAdd)
        btnLogout = findViewById(R.id.btnLogOut)

        recyclerView.layoutManager = LinearLayoutManager(this)
        projectList = arrayListOf()
        adapter = ProjectAdapter(projectList, this)
        recyclerView.adapter = adapter

        sessionManager = SessionManager(this)
        api = ApiClient.getApiService(this)

        fetchProjects()

        btnLogout.setOnClickListener {
            sessionManager.clearSession()
            Toast.makeText(this, "Anda telah logout", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnAdd.setOnClickListener {
            showProjectDialog()
        }
    }

    private fun fetchProjects() {
        progressBar.visibility = View.VISIBLE
        api.getProjects().enqueue(object : Callback<List<Project>> {
            override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    projectList.clear()
                    response.body()?.let { projectList.addAll(it) }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                progressBar.visibility = View.GONE
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
            name.setText(project.Name)
            description.setText(project.description)
            startDate.setText(project.start_date)
            endDate.setText(project.end_date)
            budget.setText(project.budget.toString())
            status.setSelection(project.status)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (project == null) "Tambah Proyek" else "Edit Proyek")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                createProject(
                    id = project?.id ?: 0,
                    name = name.text.toString(),
                    description = description.text.toString(),
                    start_date = startDate.text.toString(),
                    end_date = endDate.text.toString(),
                    status = status.selectedItemPosition,
                    budget = budget.text
                )
            }
            .setNegativeButton("Batal", null)
            .create()
        dialog.show()
    }

    private fun createProject(
        id: Int,
        name: String,
        description: String,
        start_date: String,
        end_date: String,
        status: Int,
        budget: Editable
    ) {
        // Convert budget to Int, menggunakan 0 jika tidak valid
        val parsedBudget = budget.toString().toIntOrNull() ?: 0
        lifecycleScope.launch {
            try {
                val api = ApiClient.getApiService(this@ManagerMainActivity)
                val projectData = Project(
                    id = id,
                    Name = name,
                    description = description,
                    start_date = start_date,
                    end_date = end_date,
                    status = status ?: 1,
                    budget = parsedBudget,  // Gunakan parsedBudget yang bertipe Int
                    manager_id = 1 // atau ambil dari sessionManager
                )

                // Convert projectData ke format JSON (raw request)
                val rawJsonRequest = """
                {
                    "id": ${projectData.id},
                    "Name": "${projectData.Name}",
                    "description": "${projectData.description}",
                    "start_date": "${projectData.start_date}",
                    "end_date": "${projectData.end_date}",
                    "status": ${projectData.status},
                    "budget": ${projectData.budget},
                    "manager_id": ${projectData.manager_id}
                }
            """.trimIndent()

                // Menampilkan raw request JSON ke tvErr
                tvErr.text = rawJsonRequest
                Log.d("RAW_REQUEST", "Request JSON: $rawJsonRequest")

                // Kode untuk mengirimkan data ke API
                val response = api.createProject(projectData)

                if (response.isSuccessful) {
                    Toast.makeText(this@ManagerMainActivity, "Proyek berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    fetchProjects()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Gagal: $errorBody")

                    tvErr.text = "Error Body: $errorBody"
                    Toast.makeText(this@ManagerMainActivity, "Gagal menambahkan proyek:\n$errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Terjadi kesalahan: ${e.message}", e)
                Toast.makeText(this@ManagerMainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                tvErr1.text = e.message
            }
        }
    }





    fun updateProject(project: Project) {
            api.updateProject(project.id, project).enqueue(object : Callback<Project> {
                override fun onResponse(call: Call<Project>, response: Response<Project>) {
                    fetchProjects()
                    Toast.makeText(
                        this@ManagerMainActivity,
                        "Proyek diperbarui",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailure(call: Call<Project>, t: Throwable) {
                    Toast.makeText(
                        this@ManagerMainActivity,
                        "Gagal update proyek",
                        Toast.LENGTH_SHORT
                    ).show()
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
