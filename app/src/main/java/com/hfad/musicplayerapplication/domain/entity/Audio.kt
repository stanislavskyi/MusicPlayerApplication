package com.hfad.musicplayerapplication.domain.entity

import android.graphics.Bitmap
import android.net.Uri

data class Audio(
    val title: String? = null,
    var imageLong: Bitmap? = null,
    val uri: Uri? = null
)
