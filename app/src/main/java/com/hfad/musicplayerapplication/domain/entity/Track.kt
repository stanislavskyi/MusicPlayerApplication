package com.hfad.musicplayerapplication.domain.entity

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val id: Long,
    val title: String,
    val duration: Int,
    val link: String,
    val preview: String,
    val md5_image: String,
    val cover_xl: String,

    val artist: String,
    val uri: Uri? = null
): Parcelable