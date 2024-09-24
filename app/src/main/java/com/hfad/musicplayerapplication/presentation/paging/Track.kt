package com.hfad.musicplayerapplication.presentation.paging

data class Track(
    val id: Long,
    val title: String,
    val duration: Int,
    val link: String,
    val preview: String,
    val md5_image: String,
    val album: Album
)
