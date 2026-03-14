package com.example.madproject.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.madproject.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** all possible states the login screen can be in. */
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

/**
 * handles login logic & holds ui state
 * extends androidviewmodel to get app context <- needed for repos
 * viewmodelscope tied to viewmodel lifetime not activity <- survives rotation, running coroutines continue until user leaves SCREEN
 */

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()

    // _state  mutable internally, state is read-only externally <- suggested by anthropic ai
    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = LoginState.Error("Please fill in all fields")
            return
        }
        viewModelScope.launch {
            _state.value = LoginState.Loading
            try {
                authRepository.login(email, password)
                _state.value = LoginState.Success
            } catch (e: Exception) {
                _state.value = LoginState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun resetState() { _state.value = LoginState.Idle }
}
