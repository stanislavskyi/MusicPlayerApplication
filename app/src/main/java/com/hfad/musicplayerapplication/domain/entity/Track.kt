package com.hfad.musicplayerapplication.domain.entity

data class Track(
    val id: Long,
    val title: String,
    val duration: Int,
    val link: String,
    val preview: String,
    val md5_image: String,
    val cover_xl: String
)