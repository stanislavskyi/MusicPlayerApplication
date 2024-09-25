package com.hfad.musicplayerapplication.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.domain.Audio

class MusicAdapter : ListAdapter<Audio, MusicAdapter.MusicViewHolder>(MusicDiffCallback()) {

    class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val musicTitle: TextView = itemView.findViewById(R.id.musicTitle)
        val preview: ImageView = itemView.findViewById(R.id.preview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
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

//        holder.preview.load(audio.imageLong) {
//            placeholder(R.drawable.music_note_48px) // Placeholder image
//            error(R.drawable.music_note_48px) // Error image
//            transformations(CircleCropTransformation()) // Optional: to apply any transformation
//        }
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