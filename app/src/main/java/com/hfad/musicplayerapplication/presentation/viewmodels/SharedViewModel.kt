package com.hfad.musicplayerapplication.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfad.musicplayerapplication.domain.entity.Music
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(): ViewModel() {

    // LiveData для текущего трека
    private val _currentTrack = MutableLiveData<Music>()
    val currentTrack: LiveData<Music> = _currentTrack

    // Функция для обновления текущего трека
    fun updateTrack(track: Music) {
        _currentTrack.value = track
    }
    
}