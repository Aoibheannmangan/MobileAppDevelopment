package com.example.madproject.ui.signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.madproject.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SignupState {
    object Idle : SignupState()
    object Loading : SignupState()
    object Success : SignupState()
    data class Error(val message: String) : SignupState()
}

class SignupViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()

    private val _state = MutableStateFlow<SignupState>(SignupState.Idle)
    val state: StateFlow<SignupState> = _state.asStateFlow()

    fun signup(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = SignupState.Error("Please fill in all fields")
            return
        }
        viewModelScope.launch {
            _state.value = SignupState.Loading
            try {
                authRepository.signup(email, password)
                _state.value = SignupState.Success
            } catch (e: Exception) {
                _state.value = SignupState.Error(e.message ?: "Signup failed")
            }
        }
    }
}
