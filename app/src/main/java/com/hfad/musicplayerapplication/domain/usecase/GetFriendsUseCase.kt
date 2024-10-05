package com.hfad.musicplayerapplication.domain.usecase

import com.hfad.musicplayerapplication.domain.entity.Friend
import com.hfad.musicplayerapplication.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow

class GetFriendsUseCase(private val repository: AccountRepository) {
    operator fun invoke(userId: String): Flow<List<Friend>> {
        return repository.getFriends(userId)
    }
}