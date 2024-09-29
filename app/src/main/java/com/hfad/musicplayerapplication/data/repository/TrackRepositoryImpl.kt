package com.hfad.musicplayerapplication.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.hfad.musicplayerapplication.data.mapper.TrackMapper
import com.hfad.musicplayerapplication.data.network.DeezerApiService
import com.hfad.musicplayerapplication.data.paging.TrackPagingSource
import com.hfad.musicplayerapplication.domain.entity.Track
import com.hfad.musicplayerapplication.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrackRepositoryImpl @Inject constructor(
    private val mapper: TrackMapper,
   private val apiService: DeezerApiService
): TrackRepository {

    override fun getTracks(): Flow<PagingData<Track>> { //с flow {} не сработало. Видимо, разный билдер
        return Pager(PagingConfig(pageSize = 20)){
            TrackPagingSource(apiService)
        }.flow.map { pagingData ->
            pagingData.map { trackDto ->
                mapper.mapDtoToEntity(trackDto)
            }
        }
    }
}