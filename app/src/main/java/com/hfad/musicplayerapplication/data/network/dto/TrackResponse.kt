package com.hfad.musicplayerapplication.data.network.dto

data class TrackResponse(
    val data: List<TrackDto>,
    val total: Int
)