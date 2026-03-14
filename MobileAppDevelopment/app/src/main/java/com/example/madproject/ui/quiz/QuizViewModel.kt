package com.example.madproject.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.madproject.data.local.WordEntity
import com.example.madproject.data.repository.AuthRepository
import com.example.madproject.data.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class QuizResult { CORRECT, INCORRECT }

data class QuizUiState(
    val currentWord: WordEntity? = null,
    val result: QuizResult? = null,
    val isLoading: Boolean = true,
    val noWordsAvailable: Boolean = false
)

//.first() gives a one-time snapshot of the word list, so it doesnt observe it continuously
class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val wordRepository = WordRepository(application)
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadNextWord()
    }

    fun loadNextWord() {
        val userId = authRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, result = null) }
            val words = wordRepository.getWords(userId).first()
            if (words.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, noWordsAvailable = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, currentWord = words.random()) }
            }
        }
    }

    fun checkAnswer(userAnswer: String) {
        val correct = _uiState.value.currentWord?.wordText ?: return
        val result = if (userAnswer.trim().equals(correct, ignoreCase = true)) {
            QuizResult.CORRECT
        } else {
            QuizResult.INCORRECT
        }
        _uiState.update { it.copy(result = result) }
    }
}
