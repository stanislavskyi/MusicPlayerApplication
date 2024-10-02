package com.hfad.musicplayerapplication.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.domain.entity.Friend

class FriendsAdapter(
    private val onItemClicked: (Friend) -> Unit
) :ListAdapter<Friend, FriendsAdapter.FriendsViewHolder>(FriendsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_account, parent, false)
        return FriendsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val item = getItem(position)
        holder.musicTitle.text = item.name
        holder.bind(item)

        holder.friendAddButton.isEnabled = item.state


    }

    inner class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val musicTitle: TextView = itemView.findViewById(R.id.nameFriend)
        val friendAddButton: Button = itemView.findViewById(R.id.friendAddButton)

        fun bind(item: Friend) {
            itemView.findViewById<Button>(R.id.friendAddButton).setOnClickListener {
                onItemClicked(item)
            }
        }
    }

    class FriendsDiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }
}