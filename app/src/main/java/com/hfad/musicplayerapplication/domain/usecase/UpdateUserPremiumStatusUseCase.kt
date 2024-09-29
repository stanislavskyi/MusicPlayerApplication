package com.hfad.musicplayerapplication.domain.usecase

import com.hfad.musicplayerapplication.domain.repository.TrackRepository
import com.hfad.musicplayerapplication.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserPremiumStatusUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String){
        repository.updateUserPremiumStatus(userId)
    }
}
