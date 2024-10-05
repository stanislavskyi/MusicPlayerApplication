package com.hfad.musicplayerapplication.domain.usecase

import com.hfad.musicplayerapplication.domain.entity.Friend
import com.hfad.musicplayerapplication.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow

class GetAllUsersUseCase(private val repository: AccountRepository) {
    operator fun invoke(): Flow<List<Friend>> {
        return repository.getAllUsers()
    }
}