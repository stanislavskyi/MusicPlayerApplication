package com.hfad.musicplayerapplication.domain.usecase

import com.hfad.musicplayerapplication.domain.repository.TrackRepository
import javax.inject.Inject

class GetTracksUseCase @Inject constructor (
    private val repository: TrackRepository
) {
    operator fun invoke() = repository.getTracks()
}