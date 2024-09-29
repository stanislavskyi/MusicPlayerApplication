package com.hfad.musicplayerapplication.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.musicplayerapplication.domain.repository.UserRepository
import com.hfad.musicplayerapplication.domain.usecase.GetCurrentUserIdUseCase
import com.hfad.musicplayerapplication.domain.usecase.UpdateUserPremiumStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val updateUserPremiumStatusUseCase: UpdateUserPremiumStatusUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    fun updateUserPremiumStatus() {
        val userId = getCurrentUserIdUseCase()
        //val userId = userRepository.getCurrentUserId()

        userId?.let {
            viewModelScope.launch {
                updateUserPremiumStatusUseCase(it)
            }
        }
    }
}
