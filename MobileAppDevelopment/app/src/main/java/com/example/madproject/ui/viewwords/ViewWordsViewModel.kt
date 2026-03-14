package com.example.madproject.ui.viewwords

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.madproject.data.local.WordEntity
import com.example.madproject.data.repository.AuthRepository
import com.example.madproject.data.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * ViewWordsViewModel exposes the user's word list as a Flow.
 *
 * Room's Flow keeps emitting updated lists as words are added,
 * so if a user adds a word and comes back to this screen, the list
 * is already up-to-date with no extra network call needed.
 */
class ViewWordsViewModel(application: Application) : AndroidViewModel(application) {

    private val wordRepository = WordRepository(application)
    private val authRepository = AuthRepository()

    /**
     * Live stream of the current user's words from local Room database.
     * The Activity collects this Flow and updates the adapter whenever it emits.
     */
    val words: Flow<List<WordEntity>> = authRepository.getCurrentUserId()
        ?.let { userId -> wordRepository.getWords(userId) }
        ?: emptyFlow()
}
