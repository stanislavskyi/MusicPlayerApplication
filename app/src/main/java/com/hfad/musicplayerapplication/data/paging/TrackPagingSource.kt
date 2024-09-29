package com.hfad.musicplayerapplication.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hfad.musicplayerapplication.data.network.DeezerApiService
import com.hfad.musicplayerapplication.data.network.dto.TrackDto

class TrackPagingSource(
    private val apiService: DeezerApiService
) : PagingSource<Int, TrackDto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TrackDto> {
        return try {
            val currentPage = params.key ?: 0
            val response = apiService.getTracks(currentPage, params.loadSize)

            Log.d("TrackPagingSource", "Downloaded response: $response")
            Log.d("TrackPagingSource", "Downloaded currentPage: $currentPage")
            LoadResult.Page(
                data = response.data,
                prevKey = if (currentPage == 0) null else currentPage - params.loadSize,
                nextKey = if (response.data.isEmpty()) null else currentPage + params.loadSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TrackDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}