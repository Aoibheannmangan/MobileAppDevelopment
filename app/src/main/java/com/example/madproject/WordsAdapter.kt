package com.example.madproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * WordsAdapter creates a card view for each words
 * Puts in the words data (but only the important stuff)
 *
 * @param words The list of Word objects fetched from Supabase to display
 */
class WordsAdapter(private val words: List<Word>) :
    RecyclerView.Adapter<WordsAdapter.WordViewHolder>() {

    /**
     * WordViewHolder caches the references to the views inside each word card
     * Avoids repeated findViewById calls when scrolling
     * Improves performance cause oh yeah
     */
    class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordText: TextView = view.findViewById(R.id.wordText)
        val wordMeaning: TextView = view.findViewById(R.id.wordMeaning)
        val bookFoundIn: TextView = view.findViewById(R.id.bookFoundIn)
    }

    //Called when the RecyclerView needs a  new card view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    //Called to populate a card with data at a given position in the list. Binds the word's text, meaning, and book name to the card's TextViews.
    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]
        holder.wordText.text = word.wordText
        holder.wordMeaning.text = word.wordMeaning
        holder.bookFoundIn.text = "Found in: ${word.bookFoundIn}"
    }

    //Tells it how many items are in the list to let it know how many cards it needs to make
    override fun getItemCount() = words.size
}