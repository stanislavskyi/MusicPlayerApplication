package com.hfad.musicplayerapplication.domain

import android.graphics.Bitmap
import android.net.Uri

data class Audio(
    val title: String? = null,
    val imageLong: Bitmap? = null,
    val uri: Uri? = null
)
