package com.hfad.musicplayerapplication.domain.repository

interface UserRepository {
    suspend fun updateUserPremiumStatus(userId: String)
    fun getCurrentUserId(): String?
}