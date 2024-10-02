package com.hfad.musicplayerapplication.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hfad.musicplayerapplication.domain.repository.UserRepository
import com.hfad.musicplayerapplication.presentation.MainActivity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository {
    override suspend fun updateUserPremiumStatus(userId: String) {

        val name = auth.currentUser?.email ?: ""

        val user = hashMapOf(
            "isPremium" to true,
            "subscriptionDate" to FieldValue.serverTimestamp(),
            "name" to  name
        )
        firestore.collection("users").document(userId).set(
            user,
            SetOptions.merge()
        ).await()
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}