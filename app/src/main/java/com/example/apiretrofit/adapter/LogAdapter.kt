package com.example.apiretrofit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apiretrofit.R
import com.example.apiretrofit.adapter.ProjectAdapter.ProjectViewHolder
import com.example.apiretrofit.api.model.LogResponse

class LogAdapter (
    private val logs: List<LogResponse>
) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {
    inner class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtActivity: TextView = itemView.findViewById(R.id.txtActivity)
        val txtActivityTime: TextView = itemView.findViewById(R.id.txtActivityTime)
        val txtUser: TextView = itemView.findViewById(R.id.txtUser)
        val txtTask: TextView = itemView.findViewById(R.id.txtTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return LogViewHolder(view)
    }

    override fun getItemCount(): Int = logs.size

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = logs[position]
        holder.txtActivity.text = log.activity
        holder.txtActivityTime.text = log.activity_time
        holder.txtUser.text = log.user_name
        holder.txtTask.text = log.task_name
    }
}