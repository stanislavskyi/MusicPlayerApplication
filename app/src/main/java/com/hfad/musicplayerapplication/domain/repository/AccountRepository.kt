package com.hfad.musicplayerapplication.domain.repository

import androidx.lifecycle.LiveData
import com.hfad.musicplayerapplication.domain.entity.Friend
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getFriends(userId: String): Flow<List<Friend>>
    fun getAllUsers(): Flow<List<Friend>>
}