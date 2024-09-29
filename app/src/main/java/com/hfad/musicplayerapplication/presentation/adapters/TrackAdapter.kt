package com.hfad.musicplayerapplication.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.domain.entity.Track
import com.squareup.picasso.Picasso

class TrackAdapter : PagingDataAdapter<Track, TrackAdapter.TrackViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_top_music, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = getItem(position)
        if (track != null) {
            holder.bind(track)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Track>() {
            override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
                return oldItem == newItem
            }
        }
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val duration: TextView = itemView.findViewById(R.id.duration)
        private val preview: ImageView = itemView.findViewById(R.id.preview)

        fun bind(trackDto: Track) {
            title.text = trackDto.title
            duration.text = trackDto.title
            Picasso.get().load("https://e-cdns-images.dzcdn.net/images/cover/${trackDto.md5_image}/250x250.jpg").into(preview)
        }
    }
}