package com.bedessee.salesca.customview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bedessee.salesca.R
import com.bedessee.salesca.modal.SalesPerson

class UserListAsapter(private val userList:List<SalesPerson>,private val onItemClick: (SalesPerson) -> Unit):
    RecyclerView.Adapter<UserListAsapter.UserViewHolder>() {
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextview: TextView = itemView.findViewById(R.id.name)
        val emailTextView: TextView = itemView.findViewById(R.id.email)
        // Add more views for other user properties as needed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item_layout, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.emailTextView.text = "Email: ${currentUser.email}"
        holder.nameTextview.text = "Name: ${currentUser.name}"

        holder.itemView.setOnClickListener {
            onItemClick(currentUser)
        }
        // Bind other user properties to their respective views
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}