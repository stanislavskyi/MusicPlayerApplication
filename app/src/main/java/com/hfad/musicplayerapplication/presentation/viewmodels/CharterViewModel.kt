package com.hfad.musicplayerapplication.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.hfad.musicplayerapplication.domain.usecase.GetTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CharterViewModel @Inject constructor(
    private val getTracksUseCase: GetTracksUseCase
) : ViewModel() {
    val tracks = getTracksUseCase().cachedIn(viewModelScope)
}