package com.example.apiretrofit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apiretrofit.R
import com.example.apiretrofit.api.model.SelectableUser

class UserSelectionAdapter(
    private val userList: List<SelectableUser>
) : RecyclerView.Adapter<UserSelectionAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.userCheckBox)
        val nameText: TextView = view.findViewById(R.id.userName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_cb, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val selectableUser = userList[position]
        holder.nameText.text = selectableUser.user.name
        holder.checkBox.isChecked = selectableUser.isSelected

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            selectableUser.isSelected = isChecked
        }
    }

    override fun getItemCount(): Int = userList.size
}
