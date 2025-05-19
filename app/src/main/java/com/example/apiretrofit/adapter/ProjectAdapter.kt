package com.example.apiretrofit.adapter

import com.example.apiretrofit.ui.manager.ManagerMainActivity
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.apiretrofit.R
import com.example.apiretrofit.api.model.Project
import com.example.apiretrofit.ui.manager.ManagerMenuActivity
import com.example.apiretrofit.ui.manager.TaskActivity

class ProjectAdapter(
    private val projects: List<Project>,
    private val context: ManagerMainActivity
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtProjectName)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
        val txtDates: TextView = itemView.findViewById(R.id.txtDates)
        val txtBudget: TextView = itemView.findViewById(R.id.txtBudget)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun getItemCount(): Int = projects.size

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.txtName.text = project.name
        holder.txtStatus.text = "Status: ${project.status}"
        holder.txtDates.text = "Mulai: ${project.start_date.takeIf { it.length >= 10 }?.substring(0, 10) ?: ""} | Selesai: ${project.end_date.takeIf { it.length >= 10 }?.substring(0, 10) ?: ""}"
        holder.txtBudget.text = "Anggaran: Rp${project.budget}"

        holder.btnEdit.setOnClickListener {
            context.showProjectDialog(project)
        }

        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Hapus Proyek")
                .setMessage("Yakin ingin menghapus proyek ini?")
                .setPositiveButton("Ya") { _, _ ->
                    context.deleteProject(project)
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ManagerMenuActivity::class.java)
            intent.putExtra("project_id", project.id)
            intent.putExtra("project_name", project.name)
            context.startActivity(intent)
        }
    }
}
