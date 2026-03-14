package com.example.madproject.ui.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun QuizScreen(
    onHomeClick: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var answerInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Quiz", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        when {
            state.isLoading -> CircularProgressIndicator()

            state.noWordsAvailable -> Text("No words saved yet! Add some words first.")

            state.currentWord != null -> {
                Text(
                    "What word means:\n\n\"${state.currentWord!!.wordMeaning}\"",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = answerInput,
                    onValueChange = { answerInput = it },
                    label = { Text("Your answer") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.result == null
                )
                Spacer(modifier = Modifier.height(16.dp))

                state.result?.let { result ->
                    Text(
                        text = when (result) {
                            QuizResult.CORRECT -> "Correct!"
                            QuizResult.INCORRECT -> "Incorrect. The word was: ${state.currentWord!!.wordText}"
                        },
                        color = if (result == QuizResult.CORRECT) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            answerInput = ""
                            viewModel.loadNextWord()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Next Word") }
                } ?: Button(
                    onClick = { viewModel.checkAnswer(answerInput) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Check") }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(onClick = onHomeClick, modifier = Modifier.fillMaxWidth()) {
            Text("Home")
        }
    }
}
