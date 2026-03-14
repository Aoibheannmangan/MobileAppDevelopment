package com.example.madproject.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.madproject.R
import com.example.madproject.ui.home.HomeActivity
import kotlinx.coroutines.launch


 // QuizActivity shows the meaning of a saved word and asks the user to type the word.

class QuizActivity : AppCompatActivity() {

    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        val meaningPrompt = findViewById<TextView>(R.id.quizMeaningPrompt)
        val answerInput = findViewById<EditText>(R.id.quizAnswerInput)
        val checkButton = findViewById<Button>(R.id.quizCheckButton)
        val nextButton = findViewById<Button>(R.id.quizNextButton)
        val resultText = findViewById<TextView>(R.id.quizResultText)
        val homeButton = findViewById<Button>(R.id.homeButton)

        homeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        checkButton.setOnClickListener {
            viewModel.checkAnswer(answerInput.text.toString())
        }

        //Reset input and result before the next word is loaded
        nextButton.setOnClickListener {
            answerInput.text.clear()
            resultText.visibility = View.GONE
            viewModel.loadNextWord()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when {
                        state.isLoading -> meaningPrompt.text = "Loading..."
                        state.noWordsAvailable -> {
                            meaningPrompt.text = "No words saved yet! Add some words first."
                            checkButton.isEnabled = false
                        }
                        state.currentWord != null -> {
                            meaningPrompt.text = "What word means:\n\n\"${state.currentWord.wordMeaning}\""
                            checkButton.isEnabled = true
                        }
                    }

                    state.result?.let { result ->
                        resultText.visibility = View.VISIBLE
                        resultText.text = when (result) {
                            QuizResult.CORRECT -> "Correct!"
                            QuizResult.INCORRECT -> "Incorrect. The word was: ${state.currentWord?.wordText}"
                        }
                        nextButton.visibility = View.VISIBLE
                        checkButton.isEnabled = false
                    } ?: run {
                        nextButton.visibility = View.GONE
                    }
                }
            }
        }
    }
}
