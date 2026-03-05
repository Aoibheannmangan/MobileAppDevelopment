package com.example.madproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import android.widget.Button

/**
 * ViewWordActivity shows all the words logged in SupaBase
 * But only the ones that the logged in user inputted
 * Cause thats what makes sense
 */

class ViewWordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_words)

        val recyclerView = findViewById<RecyclerView>(R.id.wordsRecyclerView)
        val homeButton = findViewById<Button>(R.id.homeButton)
        //Put the words in a vertical list thats scrollable cause there might be a lot of words
        recyclerView.layoutManager = LinearLayoutManager(this)

        homeButton.setOnClickListener {
            startActivity(Intent(this@ViewWordActivity, HomeActivity::class.java))
        }
        lifecycleScope.launch {
            try {
                val currentUser = SupabaseClientProvider.client.auth.currentUserOrNull()
                val userId = currentUser?.id

                if (userId == null) {
                    Toast.makeText(this@ViewWordActivity, "Not logged in", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                //Get all the words from the supabase but ONLY for the one user logged in cause we dont want anyone elses words
               //RLS policy on words means only user's words are returned
                val words = SupabaseClientProvider.client
                    .from("words")
                    .select{
                        filter {
                            eq("user_id", userId)
                        }
                    }
                    .decodeList<Word>()

                //Set the adapter with the fetched words
                recyclerView.adapter = WordsAdapter(words)
            } catch (e: Exception) {
                android.util.Log.e("ViewWordsError", "Failed to load words", e)
                Toast.makeText(this@ViewWordActivity, "Failed to load words: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}