package com.hfad.musicplayerapplication.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.musicplayerapplication.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
): ViewModel() {

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit){
        viewModelScope.launch {
            val result = loginUseCase(email, password)
            if (result.isSuccess){
                onSuccess()
            }else{
                onError("Login failed")
                //onError(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }
}