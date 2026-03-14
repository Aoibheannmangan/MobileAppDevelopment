package com.example.madproject.ui.viewwords

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * LazyColumn is Compose's equivalent of RecyclerView.
 * It only composes and renders items that are currently visible on screen —
 * exactly like RecyclerView's view recycling, but with no adapter boilerplate.
 *
 * items(words) { word -> ... } is equivalent to onBindViewHolder.
 * WordsAdapter.kt is no longer needed.
 */
@Composable
fun ViewWordsScreen(
    onHomeClick: () -> Unit,
    viewModel: ViewWordsViewModel = viewModel()
) {
    // collectAsState() on a Flow<List<WordEntity>> — recomposes whenever Room emits a new list
    val words by viewModel.words.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("My Words", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(words) { word ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(word.wordText, style = MaterialTheme.typography.titleMedium)
                        Text(word.wordMeaning, style = MaterialTheme.typography.bodySmall)
                        Text("From: ${word.bookFoundIn}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onHomeClick, modifier = Modifier.fillMaxWidth()) {
            Text("Home")
        }
    }
}
