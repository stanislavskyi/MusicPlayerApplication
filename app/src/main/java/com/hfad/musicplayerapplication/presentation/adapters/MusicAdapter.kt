package com.hfad.musicplayerapplication.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.domain.Audio

class MusicAdapter(
    private val onItemClicked: (Audio) -> Unit
) : ListAdapter<Audio, MusicAdapter.MusicViewHolder>(MusicDiffCallback()) {

    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val musicTitle: TextView = itemView.findViewById(R.id.musicTitle)
        val preview: ImageView = itemView.findViewById(R.id.preview)

        fun bind(item: Audio) {
            itemView.setOnClickListener {
                onItemClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_music,
            parent,
            false
        )
        return MusicViewHolder(view)
    }




    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val audio = getItem(position)
        if (audio.title != null) {
            holder.musicTitle.text = audio.title
        } else{
            holder.musicTitle.text = "null"
        }

        if (audio.imageLong != null) {
            holder.preview.setImageBitmap(audio.imageLong)
        } else {
            holder.preview.setImageResource(R.drawable.ic_launcher_background) // Убедитесь, что у вас есть изображение по умолчанию
        }

        holder.bind(audio)

    }

    class MusicDiffCallback : DiffUtil.ItemCallback<Audio>() {
        override fun areItemsTheSame(oldItem: Audio, newItem: Audio): Boolean {
            return oldItem.imageLong == newItem.imageLong
        }

        override fun areContentsTheSame(oldItem: Audio, newItem: Audio): Boolean {
            return oldItem == newItem
        }
    }
}