package com.example.madproject.ui.viewwords

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madproject.R
import com.example.madproject.data.local.WordEntity

/**
 * WordsAdapter  takes a List<WordEntity> (from Room)

 */
class WordsAdapter(private val words: List<WordEntity>) :
    RecyclerView.Adapter<WordsAdapter.WordViewHolder>() {

    class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordText: TextView = view.findViewById(R.id.wordText)
        val wordMeaning: TextView = view.findViewById(R.id.wordMeaning)
        val bookFoundIn: TextView = view.findViewById(R.id.bookFoundIn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]
        holder.wordText.text = word.wordText
        holder.wordMeaning.text = word.wordMeaning
        holder.bookFoundIn.text = "Found in: ${word.bookFoundIn}"
    }

    override fun getItemCount() = words.size
}
