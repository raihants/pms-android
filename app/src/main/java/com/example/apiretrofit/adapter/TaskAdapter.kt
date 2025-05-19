package com.example.apiretrofit.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.apiretrofit.R
import com.example.apiretrofit.api.model.TaskResponse
import com.example.apiretrofit.ui.manager.TaskActivity

class TaskAdapter(
    private val taskList: List<TaskResponse>,
    private val context: TaskActivity,
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.textTaskName)
        val priority: TextView = itemView.findViewById(R.id.textPriority)
        val date: TextView = itemView.findViewById(R.id.textDate)
        val status: TextView = itemView.findViewById(R.id.textStatus)
        val tugas : TextView = itemView.findViewById(R.id.textDitugas)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskName.text = task.taskName
        holder.priority.text = "Priority: ${task.priority}"
        holder.date.text = "Mulai: ${task.startDate.takeIf { it.length >= 10 }?.substring(0, 10) ?: ""} | Selesai: ${task.endDate.takeIf { it.length >= 10 }?.substring(0, 10) ?: ""}"
        holder.status.text = "Status: ${task.status}"
        holder.tugas.text = "Di tugaskan ke: ${task.userName}"

        // Atur warna berdasarkan prioritas
        val color = when (task.priority.lowercase()) {
            "tinggi" -> ContextCompat.getColor(context, android.R.color.holo_red_dark)
            "sedang" -> ContextCompat.getColor(context, android.R.color.holo_orange_dark)
            "rendah" -> ContextCompat.getColor(context, android.R.color.holo_green_dark)
            else -> ContextCompat.getColor(context, android.R.color.darker_gray)
        }

        holder.cardView.setCardBackgroundColor(color)

        holder.btnEdit.setOnClickListener {
            context.showTaskDialog(task)
        }

        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Hapus Proyek")
                .setMessage("Yakin ingin menghapus proyek ini?")
                .setPositiveButton("Ya") { _, _ ->
                    context.deleteTask(task)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun getItemCount(): Int = taskList.size
}
