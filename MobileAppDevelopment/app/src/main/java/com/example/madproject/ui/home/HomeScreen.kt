package com.example.madproject.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * HomeScreen is just a nav hub — no ViewModel needed, same as before.
 * Navigation is now pure lambdas passed in from AppNavGraph.
 */
@Composable
fun HomeScreen(
    onAddWord: () -> Unit,
    onViewWords: () -> Unit,
    onQuiz: () -> Unit,
    onDeviceStats: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Word Logger", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onAddWord, modifier = Modifier.fillMaxWidth()) { Text("Add Word") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onViewWords, modifier = Modifier.fillMaxWidth()) { Text("View Words") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onQuiz, modifier = Modifier.fillMaxWidth()) { Text("Quiz") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onDeviceStats, modifier = Modifier.fillMaxWidth()) { Text("Device Stats") }
    }
}
