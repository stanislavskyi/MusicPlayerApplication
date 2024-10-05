package com.hfad.musicplayerapplication.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hfad.musicplayerapplication.domain.entity.Friend
import com.hfad.musicplayerapplication.domain.usecase.GetAllUsersUseCase
import com.hfad.musicplayerapplication.domain.usecase.GetFriendsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject

// AccountViewModel.kt
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getFriendsUseCase: GetFriendsUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends

    private val _allUsers = MutableStateFlow<List<Friend>>(emptyList())
    val allUsers: StateFlow<List<Friend>> = _allUsers

    init {
        loadFriends()
    }

    private fun loadFriends() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            getFriendsUseCase(userId).collect { friendsList ->
                _friends.value = friendsList
                loadAllUsers() // Перезагружаем всех пользователей после загрузки друзей
            }
        }
    }

    private fun loadAllUsers() {
        viewModelScope.launch {
            getAllUsersUseCase().collect { usersList ->
                val updatedUsers = updateFriendStates(usersList, _friends.value)
                _allUsers.value = updatedUsers
            }
        }
    }

    private fun updateFriendStates(users: List<Friend>, friends: List<Friend>): List<Friend> {
        return users.map { user ->
            if (friends.any { it.name == user.name }) {
                user.copy(state = false)
            } else {
                user
            }
        }
    }
}

