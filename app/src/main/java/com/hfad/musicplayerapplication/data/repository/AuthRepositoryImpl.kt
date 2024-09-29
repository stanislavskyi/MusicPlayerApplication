package com.hfad.musicplayerapplication.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.hfad.musicplayerapplication.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
    override suspend fun register(email: String, password: String): Result<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(true)
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}