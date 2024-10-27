package com.hfad.musicplayerapplication.presentation.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfad.musicplayerapplication.domain.entity.Music
import com.hfad.musicplayerapplication.domain.entity.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(): ViewModel() {

    // LiveData для текущего трека
    private val _currentTrack = MutableLiveData<Track>()
    val currentTrack: LiveData<Track> = _currentTrack

    // Функция для обновления текущего трека
    fun updateTrack(track: Track) {
        _currentTrack.value = track
    }
    
}