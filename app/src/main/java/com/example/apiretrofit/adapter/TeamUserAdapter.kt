package com.example.apiretrofit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apiretrofit.R
import com.example.apiretrofit.api.model.UserTeam

class TeamUserAdapter (private val userUserTeamList: List<UserTeam>) :
    RecyclerView.Adapter<TeamUserAdapter.TeamViewHolder>() {

    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvTeamName: TextView = itemView.findViewById(R.id.tvTeamName)
        val tvTeamDescription: TextView = itemView.findViewById(R.id.tvTeamDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team_user, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        val userTeam = userUserTeamList[position]
        holder.tvUserName.text = userTeam.userName
        holder.tvTeamName.text = userTeam.teamName
        holder.tvTeamDescription.text = userTeam.teamDescription
    }

    override fun getItemCount(): Int = userUserTeamList.size
}