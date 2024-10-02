package com.hfad.musicplayerapplication.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.musicplayerapplication.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
): ViewModel() {

    fun register(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit){
        viewModelScope.launch {
            val result = registerUseCase(email, password)
            if (result.isSuccess){
                onSuccess()
            }else{
                onError("Register failed")
            }
        }
    }
}