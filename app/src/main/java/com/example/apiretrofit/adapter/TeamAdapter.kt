package com.example.apiretrofit.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apiretrofit.R
import com.example.apiretrofit.api.model.Team
import com.example.apiretrofit.ui.share.TeamActivity
import com.example.apiretrofit.ui.share.TeamsUsersActivity

class TeamAdapter (
    private val teamList: List<Team>,
    private val context: TeamActivity
) :
    RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

        inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvTeamName: TextView = itemView.findViewById(R.id.tvTeamName)
            val tvTeamDescription: TextView = itemView.findViewById(R.id.tvTeamDescription)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_team, parent, false)
            return TeamViewHolder(view)
        }

        override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
            val team = teamList[position]
            holder.tvTeamName.text = team.name
            holder.tvTeamDescription.text = team.description

            holder.itemView.setOnClickListener {
                val intent = Intent(context, TeamsUsersActivity::class.java)
                intent.putExtra("project_id", team.project_id)
                intent.putExtra("team_id", team.id)
                context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int = teamList.size

    }