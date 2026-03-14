package com.example.madproject.ui.addword

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.madproject.R
import com.example.madproject.ui.home.HomeActivity
import kotlinx.coroutines.launch

class AddWordActivity : AppCompatActivity() {

    private val viewModel: AddWordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_word)

        val wordInput = findViewById<EditText>(R.id.wordInput)
        val bookInput = findViewById<EditText>(R.id.bookInput)
        val saveButton = findViewById<Button>(R.id.saveWord)
        val wordMeaningText = findViewById<TextView>(R.id.wordMeaning)
        val homeButton = findViewById<Button>(R.id.homeButton)

        homeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        saveButton.setOnClickListener {
            viewModel.saveWord(wordInput.text.toString(), bookInput.text.toString())
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is AddWordState.Loading -> saveButton.isEnabled = false
                        is AddWordState.Saved -> {
                            saveButton.isEnabled = true
                            wordMeaningText.text = "Meaning: ${state.meaning}"
                            Toast.makeText(this@AddWordActivity, "Word Saved!", Toast.LENGTH_SHORT).show()
                            viewModel.resetState()
                        }
                        is AddWordState.Error -> {
                            saveButton.isEnabled = true
                            Toast.makeText(this@AddWordActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetState()
                        }
                        is AddWordState.Idle -> saveButton.isEnabled = true
                    }
                }
            }
        }
    }
}
