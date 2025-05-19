package com.example.apiretrofit.ui.manager

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apiretrofit.R
import com.example.apiretrofit.adapter.MenuAdapter

class ManagerMenuActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var txtProjectName: TextView
    private val menuList = listOf("Task", "Log", "Teams")
    private var projectID: Int = -1
    private var projectName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manager_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        txtProjectName = findViewById(R.id.txtProjectName)
        projectID = intent.getIntExtra("project_id", -1)
        projectName = intent.getStringExtra("project_name") ?: ""
        txtProjectName.text = projectName
        recyclerView = findViewById(R.id.recyclerMenu)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = MenuAdapter(menuList) { selected ->
            when (selected) {
                "Task" -> startActivity(Intent(this, TaskActivity::class.java).putExtra("project_id", projectID))
                "Log" -> startActivity(Intent(this, LogActivity::class.java).putExtra("project_id", projectID))
                "Teams" -> startActivity(Intent(this, TeamActivity::class.java).putExtra("project_id", projectID))
            }
        }
    }
}