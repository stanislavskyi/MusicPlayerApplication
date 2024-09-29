package com.hfad.musicplayerapplication.data.mapper

import com.hfad.musicplayerapplication.data.network.dto.TrackDto
import com.hfad.musicplayerapplication.domain.entity.Track
import javax.inject.Inject

class TrackMapper @Inject constructor(){
    fun mapDtoToEntity(dto: TrackDto) = Track(
        id = dto.id,
        title = dto.title,
        duration = dto.duration,
        link = dto.link,
        preview = dto.preview,
        md5_image = dto.md5_image,
        cover_xl = dto.album.cover_xl
    )
}