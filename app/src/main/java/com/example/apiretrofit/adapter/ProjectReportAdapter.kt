package com.example.apiretrofit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apiretrofit.R
import com.example.apiretrofit.api.model.ProjectReportResponse

class ProjectReportAdapter(
    private val data: List<ProjectReportResponse>
) : RecyclerView.Adapter<ProjectReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTeamName: TextView = view.findViewById(R.id.tvTeamName)
        val tvUncompletedTasks: TextView = view.findViewById(R.id.tvUncompletedTasks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val item = data[position]
        val uncompletedTasks = item.totalTasks - item.completedTasks

        holder.tvTeamName.text = item.teamName
        holder.tvUncompletedTasks.text = "Remaining Tasks: $uncompletedTasks"
    }

    override fun getItemCount(): Int = data.size
}
