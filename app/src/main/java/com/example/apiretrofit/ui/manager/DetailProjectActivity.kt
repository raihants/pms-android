package com.example.apiretrofit.ui.manager

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
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
import com.example.apiretrofit.R
import com.example.apiretrofit.adapter.TaskAdapter
import com.example.apiretrofit.api.model.Project
import com.example.apiretrofit.api.model.TaskRequest
import com.example.apiretrofit.api.model.TaskResponse
import com.example.apiretrofit.api.model.UserByProjectID
import com.example.apiretrofit.api.services.ApiClient
import com.example.apiretrofit.api.services.ApiService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import kotlin.collections.ArrayList

class DetailProjectActivity : AppCompatActivity() {
    private lateinit var taskList: ArrayList<TaskResponse>
    private lateinit var userList: ArrayList<UserByProjectID>
    private lateinit var progressBar: ProgressBar
    private lateinit var api: ApiService
    private lateinit var buttonAdd: FloatingActionButton
    private lateinit var adapter: TaskAdapter
    private var projectID: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_project)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressBar = findViewById(R.id.progressBar)
        buttonAdd = findViewById(R.id.btnAdd)

        taskList = ArrayList()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(taskList, this)
        recyclerView.adapter = adapter

        api = ApiClient.getApiService(this)

        userList = ArrayList()

        projectID = intent.getIntExtra("project_id", -1)
        Log.d("project_id", projectID.toString())

        buttonAdd.setOnClickListener(
            { showTaskDialog() }
        )

        if (projectID != -1) {
            fetchTasks(projectID)
            fetchUsers(projectID)
        } else {
            Toast.makeText(this, "ID proyek tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun fetchTasks( id : Int) {
        progressBar.visibility = View.VISIBLE
        api.getTasks(id).enqueue(object : Callback<List<TaskResponse>> {
            override fun onResponse(call: Call<List<TaskResponse>>, response: Response<List<TaskResponse>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    taskList.clear()
                    response.body()?.let { taskList.addAll(it) }
                    Log.d("API_RESPONSE", taskList.toString())
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<TaskResponse>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@DetailProjectActivity, "Gagal ambil data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
    private fun fetchUsers( id : Int ) {
        progressBar.visibility = View.VISIBLE
        api.getUserByProjID(id).enqueue(object : Callback<List<UserByProjectID>> {
            override fun onResponse(call: Call<List<UserByProjectID>>, response: Response<List<UserByProjectID>>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    userList.clear()
                    response.body()?.let { userList.addAll(it) }
                    Log.d("API_RESPONSE", userList.toString())
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<UserByProjectID>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@DetailProjectActivity, "Gagal ambil data user", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    fun showTaskDialog(task: TaskResponse? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_task, null)
        val name = dialogView.findViewById<EditText>(R.id.editName)
        val description = dialogView.findViewById<EditText>(R.id.editDescription)
        val startDate = dialogView.findViewById<EditText>(R.id.editStartDate)
        val endDate = dialogView.findViewById<EditText>(R.id.editEndDate)
        val status = dialogView.findViewById<Spinner>(R.id.spinnerStatus)
        val priority = dialogView.findViewById<Spinner>(R.id.spinnerPrioritas)
        val diTugas = dialogView.findViewById<Spinner>(R.id.spinnerDiTugas)

        val statuses = arrayOf("Belum Dimulai", "Sedang Dikerjakan", "Selesai")
        val priorities = arrayOf("Tinggi", "Sedang", "Rendah")
        val userNames = userList.map { it.name }

        val adapterDiTugas = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, userNames)
        diTugas.adapter = adapterDiTugas
        val adapterStatus = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)
        status.adapter = adapterStatus
        val adapterPriority = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, priorities)
        priority.adapter = adapterPriority

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

        if (task != null) {
            name.setText(task.taskName)
            description.setText(task.description)
            startDate.setText(task.startDate.takeIf { it.length >= 10 }?.substring(0, 10) ?: "")
            endDate.setText(task.endDate.takeIf { it.length >= 10 }?.substring(0, 10) ?: "")
            val statusIndex = statuses.indexOfFirst { it.equals(task.status, ignoreCase = true) }
            if (statusIndex >= 0) {
                status.setSelection(statusIndex)
            }
            val priorityIndex = priorities.indexOfFirst { it.equals(task.priority, ignoreCase = true) }
            if (priorityIndex >= 0) {
                priority.setSelection(priorityIndex)
            }
            val userIndex = userList.indexOfFirst { it.name == task.userName }
            if (userIndex >= 0) {
                diTugas.setSelection(userIndex)
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (task == null) "Tambah Project" else "Edit Project")
            .setView(dialogView)
            .setPositiveButton(if (task == null) "Simpan" else "Update") { _, _ ->
                val selectedUserIndex = diTugas.selectedItemPosition
                val selectedUserId = userList.getOrNull(selectedUserIndex)?.id ?: -1 // fallback
                val selectedPriority = priority.selectedItem.toString()
                Log.e("selected", selectedUserId.toString() + selectedPriority)
                val taskData = TaskRequest(
                    projectId = projectID,
                    taskID = task?.taskID ?: 0,
                    taskName = name.text.toString(),
                    description = description.text.toString(),
                    priority = selectedPriority,
                    startDate = startDate.text.toString(),
                    endDate = endDate.text.toString(),
                    status = status.selectedItem.toString(),
                    userID = selectedUserId
                )
                Log.e("taskData", taskData.toString())

                if (task == null) {
                    createTask(taskData)
                } else {
                    updateTask(taskData)
                }
            }

            .setNegativeButton("Batal", null)
            .create()
        dialog.show()

    }

    private fun createTask(task: TaskRequest) {
        lifecycleScope.launch {
            try {
                val response = api.createTask(task)
                if (response.isSuccessful) {
                    Toast.makeText(this@DetailProjectActivity, "Task berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    fetchTasks(projectID)
                } else {
                    Toast.makeText(this@DetailProjectActivity, "Gagal menambahkan task", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailProjectActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateTask(task: TaskRequest) {
        api.updateTask(task.taskID, task).enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DetailProjectActivity, "Task diperbarui", Toast.LENGTH_SHORT).show()
                    fetchTasks(projectID)
                } else {
                    Toast.makeText(this@DetailProjectActivity, "Gagal memperbarui task", Toast.LENGTH_SHORT).show()
                    Log.e("API", "Error updating task: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Toast.makeText(this@DetailProjectActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun deleteTask(task: TaskResponse) {
        api.deleteTask(task.taskID).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("DELETE_TASK", "Status Code: ${response.code()}, task id = ${task.taskID}")
                    Toast.makeText(this@DetailProjectActivity, "Task dihapus", Toast.LENGTH_SHORT).show()
                    fetchTasks(projectID)
                } else {
                    Log.e("DELETE_TASK", "Failed to delete. Code: ${response.code()}, Message: ${response.message()}")
                    Toast.makeText(this@DetailProjectActivity, "Gagal hapus task", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@DetailProjectActivity,
                    "Gagal hapus task",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}