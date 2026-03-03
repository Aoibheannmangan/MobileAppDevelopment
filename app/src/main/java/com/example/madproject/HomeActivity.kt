package com.example.madproject

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * THIS IS JUST FOR TESTING I PROMISE ILL CHANGE IT LATER
 * Unless nobody sees this commit cause im sneaky like that
 * but i promise stuff will be added here when login/signup is 100% working
 * so slay
 */
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Simple TextView — no XML layout needed for testing
        val text = TextView(this).apply {
            text = "Login Successful! Home Screen"
            textSize = 24f
            setPadding(64, 64, 64, 64)
        }

        setContentView(text)
    }
}