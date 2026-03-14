package com.example.madproject.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity — defines the 'words' table in the local SQLite database.
 * @Entity tells Room to create a table for this class.
 * Each field becomes a column. @PrimaryKey(autoGenerate=true) means Room
 * auto-assigns a unique integer ID to each row we insert.
 *
 * userId is stored alongside each word so we can filter by the logged-in user,
 * mirroring the RLS filtering Supabase does on the remote side.
 */
@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val wordText: String,
    val wordMeaning: String,
    val bookFoundIn: String
)
