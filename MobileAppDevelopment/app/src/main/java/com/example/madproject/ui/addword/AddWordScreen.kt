package com.example.madproject.ui.addword

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AddWordScreen(
    onHomeClick: () -> Unit,
    viewModel: AddWordViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var wordInput by remember { mutableStateOf("") }
    var bookInput by remember { mutableStateOf("") }
    // Keep the last fetched meaning visible after save
    var lastMeaning by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state) {
        when (state) {
            is AddWordState.Saved -> {
                lastMeaning = (state as AddWordState.Saved).meaning
                Toast.makeText(context, "Word Saved!", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            is AddWordState.Error -> {
                Toast.makeText(context, (state as AddWordState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Add Word", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = wordInput,
            onValueChange = { wordInput = it },
            label = { Text("Word") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = bookInput,
            onValueChange = { bookInput = it },
            label = { Text("Book") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        lastMeaning?.let {
            Text("Meaning: $it", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = { viewModel.saveWord(wordInput, bookInput) },
            enabled = state !is AddWordState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state is AddWordState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp))
            } else {
                Text("Save Word")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = onHomeClick, modifier = Modifier.fillMaxWidth()) {
            Text("Home")
        }
    }
}
