package com.hfad.musicplayerapplication.domain.usecase

import com.hfad.musicplayerapplication.domain.repository.UserRepository
import javax.inject.Inject

class GetCurrentUserIdUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): String{
        return repository.getCurrentUserId() ?: ""
    }
}