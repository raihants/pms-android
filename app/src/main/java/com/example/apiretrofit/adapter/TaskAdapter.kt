package com.example.apiretrofit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.apiretrofit.R
import com.example.apiretrofit.api.model.Tasks
import com.example.apiretrofit.ui.manager.DetailProjectActivity

class TaskAdapter(
    private val taskList: List<Tasks>,
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.textTaskName)
        val priority: TextView = itemView.findViewById(R.id.textPriority)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskName.text = task.taskName
        holder.priority.text = "Priority: ${task.priority}"

        // Atur warna berdasarkan prioritas
        val context = holder.itemView.context
        val color = when (task.priority.lowercase()) {
            "tinggi" -> ContextCompat.getColor(context, android.R.color.holo_red_light)
            "sedang" -> ContextCompat.getColor(context, android.R.color.holo_orange_light)
            "rendah" -> ContextCompat.getColor(context, android.R.color.holo_green_light)
            else -> ContextCompat.getColor(context, android.R.color.darker_gray)
        }

        holder.cardView.setCardBackgroundColor(color)
    }

    override fun getItemCount(): Int = taskList.size
}
