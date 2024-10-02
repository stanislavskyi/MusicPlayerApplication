package com.hfad.musicplayerapplication.domain.usecase


import com.hfad.musicplayerapplication.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val registerRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Boolean> {
        return registerRepository.register(email, password)
    }
}