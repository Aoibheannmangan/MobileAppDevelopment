package com.example.madproject

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

/**
 * The home screen now navigates to three different screens
 * AddWords, ViewWords and Quiz
 * If more are needed will be added in the future
 */
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val addWordButton = findViewById<Button>(R.id.addWord)
        val viewWordButton = findViewById<Button>(R.id.viewWords)
        val quizButton = findViewById<Button>(R.id.quiz)

        addWordButton.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AddWordActivity::class.java))
        }

        viewWordButton.setOnClickListener {
            startActivity(Intent(this@HomeActivity, ViewWordActivity::class.java))
        }

        quizButton.setOnClickListener {
            startActivity(Intent(this@HomeActivity, QuizActivity::class.java))
        }
    }
}