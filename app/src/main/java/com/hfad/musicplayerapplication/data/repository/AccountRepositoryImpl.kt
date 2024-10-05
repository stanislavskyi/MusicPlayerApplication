package com.hfad.musicplayerapplication.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.musicplayerapplication.domain.entity.Friend
import com.hfad.musicplayerapplication.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AccountRepository {

    override fun getFriends(userId: String): Flow<List<Friend>> = flow {
        val friends = mutableListOf<Friend>()
        val result = firestore.collection("users")
            .document(userId).collection("friends").get().await()
        for (document in result) {
            friends.add(Friend(document.id))
        }
        emit(friends)
    }

    override fun getAllUsers(): Flow<List<Friend>> = flow {
        val users = mutableListOf<Friend>()
        val result = firestore.collection("users").get().await()
        for (document in result) {
            val name = document.getString("name") ?: "Unknown"
            users.add(Friend(name))
        }
        emit(users)
    }

    //Copilot
//    override fun getFriends(userId: String): Flow<List<Friend>> = flow {
//        val friends = mutableListOf<Friend>()
//        val result = firestore.collection("users")
//            .document(userId).collection("friends").get().await()
//        for (document in result) {
//            friends.add(Friend(document.id))
//        }
//        emit(friends)
//    }
//
//    override fun getAllUsers(): Flow<List<Friend>> = flow {
//        val users = mutableListOf<Friend>()
//        val result = firestore.collection("users").get().await()
//        for (document in result) {
//            val name = document.getString("name") ?: "Unknown"
//            users.add(Friend(name))
//        }
//        emit(users)
//    }

    //chatGPT
//    override fun getFriends(): Flow<List<Friend>> = flow {
//        val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
//        val friendsList = mutableListOf<Friend>()
//
//        val result = firestore.collection("users")
//            .document(userId)
//            .collection("friends")
//            .get()
//            .await()  // Convert Firebase to coroutine
//
//        for (document in result) {
//            friendsList.add(Friend(document.id))
//        }
//        emit(friendsList)
//    }
//
//    override fun getUsers(): Flow<List<Friend>> = flow {
//        val usersList = mutableListOf<Friend>()
//
//        val result = firestore.collection("users")
//            .get()
//            .await()  // Convert Firebase to coroutine
//
//        for (document in result) {
//            val name = document.getString("name") ?: "Unknown"
//            usersList.add(Friend(name))
//        }
//        emit(usersList)
//    }

//    override fun getFriends(): Flow<List<Friend>> = flow{
//
//        val userId = auth.currentUser?.uid
//
//        if (userId == null){
//            emit(emptyList())
//            return@flow
//        }
//
//        val friendsTask = firestore.collection("users")
//            .document(userId)
//            .collection("friends")
//            .get()
//            .await()  // Используем await() для получения результата Task в корутине
//
//        val friendsList = friendsTask.documents.map { Friend(it.id) }
//
//        val usersTask = firestore.collection("users").get().await()
//        val usersList = usersTask.documents.map { document ->
//            val name = document.getString("name") ?: "Unknown"
//            Friend(name)
//        }.toMutableList()
//
//        // Обновляем состояние друзей
//        usersList.forEach { user ->
//            if (friendsList.any { friend -> friend.name == user.name }) {
//                user.state = false
//            }
//        }
//
//        emit(usersList)  // Возвращаем обновленный список пользователей
//    }.catch { exception ->
//        emit(emptyList())  // В случае ошибки возвращаем пустой список
//        Log.d("AccountRepository", "Error: $exception")
//    }
}