package com.example.apiretrofit.ui.share

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
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apiretrofit.R
import com.example.apiretrofit.adapter.TaskAdapter
import com.example.apiretrofit.api.model.TaskRequest
import com.example.apiretrofit.api.model.TaskResponse
import com.example.apiretrofit.api.model.UserTeam
import com.example.apiretrofit.api.services.ApiClient
import com.example.apiretrofit.api.services.ApiService
import com.example.apiretrofit.api.session.SessionManager
import com.example.apiretrofit.ui.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import kotlin.collections.ArrayList

class TaskActivity : AppCompatActivity() {
    private lateinit var taskList: ArrayList<TaskResponse>
    private lateinit var userList: ArrayList<UserTeam>
    private lateinit var api: ApiService
    private lateinit var buttonAdd: FloatingActionButton
    private lateinit var adapter: TaskAdapter
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var sessionManager: SessionManager
    private var projectID: Int = -1
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonAdd = findViewById(R.id.btnAdd)

        taskList = ArrayList()

        sessionManager = SessionManager(this)

        val role = sessionManager.isUserRole("Manajer")

        userId = sessionManager.getUserId()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(taskList, this, role)
        recyclerView.adapter = adapter

        api = ApiClient.getApiService(this)

        userList = ArrayList()

        projectID = intent.getIntExtra("project_id", -1)
        Log.d("project_id", projectID.toString())

        buttonAdd.isVisible = role

        buttonAdd.setOnClickListener(
            { showTaskDialog() }
        )

        swipeRefresh = findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener {
            fetchTasks(projectID) // refresh task saat swipe
        }

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

    private fun fetchTasks( id: Int ) {
        swipeRefresh.isRefreshing = true
        api.getTasks(id).enqueue(object : Callback<List<TaskResponse>> {
            override fun onResponse(call: Call<List<TaskResponse>>, response: Response<List<TaskResponse>>) {
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful) {
                    taskList.clear()
                    response.body()?.let { taskList.addAll(it) }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<TaskResponse>>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@TaskActivity, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUsers( id : Int ) {
        swipeRefresh.isRefreshing = true
        api.getTeamsUsers(id).enqueue(object : Callback<List<UserTeam>> {
            override fun onResponse(call: Call<List<UserTeam>>, response: Response<List<UserTeam>>) {
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful) {
                    userList.clear()
                    response.body()?.let { userList.addAll(it) }
                    Log.d("API_RESPONSE", userList.toString())
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<UserTeam>>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@TaskActivity, "Gagal ambil data user", Toast.LENGTH_SHORT)
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
        val isManager = sessionManager.isUserRole("Manajer")
        val isAssignedUser = task != null && task.userID == userId

        val canEditAllFields = isManager
        val canOnlyUpdateStatus = !isManager && isAssignedUser


        when {
            canEditAllFields -> {
            }
            canOnlyUpdateStatus -> {
                // User hanya bisa update status
                name.isEnabled = false
                description.isEnabled = false
                startDate.isEnabled = false
                endDate.isEnabled = false
                priority.isEnabled = false
                diTugas.isEnabled = false
                status.isEnabled = true
            }
            else -> {
                // Tidak bisa edit apapun
                name.isEnabled = false
                description.isEnabled = false
                startDate.isEnabled = false
                endDate.isEnabled = false
                priority.isEnabled = false
                diTugas.isEnabled = false
                status.isEnabled = false
            }
        }


        val statuses = arrayOf("Belum Dimulai", "Sedang Dikerjakan", "Selesai")
        val priorities = arrayOf("Tinggi", "Sedang", "Rendah")
        val userNames = userList.map { it.userName }

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
            val userIndex = userList.indexOfFirst { it.userName == task.userName }
            if (userIndex >= 0) {
                diTugas.setSelection(userIndex)
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(
                when {
                    task == null -> "Tambah Task"
                    canOnlyUpdateStatus -> "Update Status"
                    else -> "Edit Task"
                }
            )
            .setView(dialogView)
            .setPositiveButton(if (task == null) "Simpan" else "Update") { _, _ ->
                val selectedUserIndex = diTugas.selectedItemPosition
                val selectedUserId = userList.getOrNull(selectedUserIndex)?.userId ?: -1
                val selectedPriority = priority.selectedItem.toString()
                val newStatus = status.selectedItem.toString()

                val taskData = if (task == null) {
                    // Tambah task (hanya boleh oleh manajer)
                    if (!sessionManager.isUserRole("Manajer")) {
                        Toast.makeText(this, "Hanya manajer yang bisa menambahkan task", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    TaskRequest(
                        susId = userId,
                        projectId = projectID,
                        taskID = 0,
                        taskName = name.text.toString(),
                        description = description.text.toString(),
                        priority = selectedPriority,
                        startDate = startDate.text.toString(),
                        endDate = endDate.text.toString(),
                        status = newStatus,
                        userID = selectedUserId
                    )
                } else {
                    // Update task
                    if (sessionManager.isUserRole("Manajer")) {
                        // Manajer bisa ubah semua field
                        TaskRequest(
                            susId = userId,
                            projectId = projectID,
                            taskID = task.taskID,
                            taskName = name.text.toString(),
                            description = description.text.toString(),
                            priority = selectedPriority,
                            startDate = startDate.text.toString(),
                            endDate = endDate.text.toString(),
                            status = newStatus,
                            userID = selectedUserId
                        )
                    } else if (canOnlyUpdateStatus) {
                        TaskRequest(
                            susId = userId,
                            projectId = task.projectId,
                            taskID = task.taskID,
                            taskName = task.taskName,
                            description = task.description,
                            priority = task.priority,
                            startDate = startDate.text.toString(),
                            endDate = endDate.text.toString(),
                            status = newStatus,
                            userID = task.userID
                        )
                    }
                    else {
                        Toast.makeText(this, "Tidak diizinkan mengedit task ini", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                }

                // Kirim ke server
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
                    Toast.makeText(this@TaskActivity, "Task berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    fetchTasks(projectID)
                } else {
                    Toast.makeText(this@TaskActivity, "Gagal menambahkan task", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TaskActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateTask(task: TaskRequest) {
        api.updateTask(task.taskID, task).enqueue(object : Callback<TaskResponse> {
            override fun onResponse(call: Call<TaskResponse>, response: Response<TaskResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@TaskActivity, "Task diperbarui", Toast.LENGTH_SHORT).show()
                    fetchTasks(projectID)
                } else {
                    Toast.makeText(this@TaskActivity, "Gagal memperbarui task", Toast.LENGTH_SHORT).show()
                    Log.e("API", "Error updating task: ${response.errorBody()?.string()}")
                    Log.d("API", task.toString())
                }
            }

            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                Toast.makeText(this@TaskActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun deleteTask(task: TaskResponse) {
        api.deleteTask(task.taskID).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("DELETE_TASK", "Status Code: ${response.code()}, task id = ${task.taskID}")
                    Toast.makeText(this@TaskActivity, "Task dihapus", Toast.LENGTH_SHORT).show()
                    fetchTasks(projectID)
                } else {
                    Log.e("DELETE_TASK", "Failed to delete. Code: ${response.code()}, Message: ${response.message()} ${response.errorBody()?.string()}")
                    Toast.makeText(this@TaskActivity, "Gagal hapus task", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(
                    this@TaskActivity,
                    "Gagal hapus task",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}