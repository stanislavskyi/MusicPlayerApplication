package com.hfad.musicplayerapplication.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.hfad.musicplayerapplication.presentation.paging.RetrofitInstance
import com.hfad.musicplayerapplication.presentation.paging.TrackPagingSource

class TrackViewModel : ViewModel() {
    val tracks = Pager(PagingConfig(pageSize = 20)) {
        TrackPagingSource(RetrofitInstance.api)
    }.flow.cachedIn(viewModelScope)
}