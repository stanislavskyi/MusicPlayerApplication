package com.hfad.musicplayerapplication.data.network.dto

data class TrackDto(
    val id: Long,
    val title: String,
    val duration: Int,
    val link: String,
    val preview: String,
    val md5_image: String,
    val album: Album
)
