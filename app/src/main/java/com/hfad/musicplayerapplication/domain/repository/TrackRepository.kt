package com.hfad.musicplayerapplication.domain.repository

import androidx.paging.PagingData
import com.hfad.musicplayerapplication.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun getTracks(): Flow<PagingData<Track>>
}