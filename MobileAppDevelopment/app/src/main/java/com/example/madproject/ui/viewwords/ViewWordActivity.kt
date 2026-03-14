package com.example.madproject.ui.viewwords

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madproject.R
import com.example.madproject.ui.home.HomeActivity
import kotlinx.coroutines.launch

/**
 * ViewWordActivity now just observes the Flow from ViewWordsViewModel.
 * When a new word is added anywhere in the app, Room emits an updated list
 * and this Activity's RecyclerView automatically refreshes — no manual reload needed.
 */
class ViewWordActivity : AppCompatActivity() {

    private val viewModel: ViewWordsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_words)

        val recyclerView = findViewById<RecyclerView>(R.id.wordsRecyclerView)
        val homeButton = findViewById<Button>(R.id.homeButton)
        recyclerView.layoutManager = LinearLayoutManager(this)

        homeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.words.collect { words ->
                    recyclerView.adapter = WordsAdapter(words)
                }
            }
        }
    }
}
