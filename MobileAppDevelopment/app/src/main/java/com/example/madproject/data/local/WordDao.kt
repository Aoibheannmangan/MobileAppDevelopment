package com.example.madproject.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * anthropic ai helped build out the word DAO and with writing the SQL especially
/ */

/**
 * Data Access Object — Is basically like the ORM
 * Room generates the actual SQL and implementation later.
 *
 * Returning Flow<List<WordEntity>> instead of just List<WordEntity> is key:
 * Room will automatically re-emit a new list every time the table changes,
 * so the UI updates reactively without us polling or refreshing manually.
 */
@Dao
interface WordDao {

    /**
     * Returns a live stream of this user's words, newest first.
     * The Flow re-emits automatically whenever a word is inserted.
     */
    @Query("SELECT * FROM words WHERE userId = :userId ORDER BY id DESC")
    fun getWordsByUser(userId: String): Flow<List<WordEntity>>

    /** Inserts a single word. suspend = must be called from a coroutine. */
    @Insert
    suspend fun insert(word: WordEntity)
}
