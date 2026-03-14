package com.example.madproject.ui.addword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.madproject.data.repository.AuthRepository
import com.example.madproject.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AddWordState {
    object Idle : AddWordState()
    object Loading : AddWordState()
    data class Saved(val meaning: String) : AddWordState()
    data class Error(val message: String) : AddWordState()
}

/**
 * AddWordViewModel coordinates fetching a word's meaning and saving it.
 *
 * All of the http stuff is in WordRepository. The ViewModel just calls
 * repository functions and updates state — the Activity just reacts to state.
 */
class AddWordViewModel(application: Application) : AndroidViewModel(application) {

    private val wordRepository = WordRepository(application)
    private val authRepository = AuthRepository()

    private val _state = MutableStateFlow<AddWordState>(AddWordState.Idle)
    val state: StateFlow<AddWordState> = _state.asStateFlow()

    fun saveWord(wordText: String, book: String) {
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            _state.value = AddWordState.Error("Not logged in")
            return
        }
        if (wordText.isBlank() || book.isBlank()) {
            _state.value = AddWordState.Error("Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _state.value = AddWordState.Loading
            try {
                // Fetch meaning from Dictionary API (network, runs on IO dispatcher inside repo)
                val meaning = wordRepository.fetchMeaning(wordText)
                // Save to Room + Supabase (also IO dispatcher inside repo)
                wordRepository.saveWord(userId, wordText, meaning, book)
                _state.value = AddWordState.Saved(meaning)
            } catch (e: Exception) {
                _state.value = AddWordState.Error(e.message ?: "Failed to save word")
            }
        }
    }

    fun resetState() { _state.value = AddWordState.Idle }
}
