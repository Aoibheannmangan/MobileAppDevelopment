package com.example.madproject.data.repository

import android.content.Context
import com.example.madproject.data.local.AppDatabase
import com.example.madproject.data.local.WordEntity
import com.example.madproject.data.remote.SupabaseClientProvider
import com.example.madproject.model.Word
import io.github.jan.supabase.postgrest.from
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray

//coordinated between Room (local), Supabase (Cloud sync) and the Dictionary API
class WordRepository(context: Context) {

    private val dao = AppDatabase.getInstance(context).wordDao()
    private val httpClient = HttpClient(Android)
    private val supabase = SupabaseClientProvider.client

    //Room re-emits automatically on every table change, so UI stays in sync for free
    fun getWords(userId: String): Flow<List<WordEntity>> {
        return dao.getWordsByUser(userId)
    }

    suspend fun fetchMeaning(word: String): String = withContext(Dispatchers.IO) {
        try {
            val response = httpClient.get("https://api.dictionaryapi.dev/api/v2/entries/en/$word")
            parseMeaning(response.bodyAsText())
        } catch (e: Exception) {
            "Meaning not found"
        }
    }

    suspend fun saveWord(userId: String, wordText: String, meaning: String, book: String) =
        withContext(Dispatchers.IO) {
            // Local first — user sees the word immediately even if network is slow
            dao.insert(WordEntity(userId = userId, wordText = wordText, wordMeaning = meaning, bookFoundIn = book))

            // Then sync to cloud
            supabase.from("words").insert(
                Word(wordText = wordText, wordMeaning = meaning, bookFoundIn = book)
            )
        }

    private fun parseMeaning(json: String): String {
        return try {
            val array = JSONArray(json)
            val meanings = array.getJSONObject(0).getJSONArray("meanings")
            val definitions = meanings.getJSONObject(0).getJSONArray("definitions")
            definitions.getJSONObject(0).getString("definition")
        } catch (e: Exception) {
            "Meaning not found"
        }
    }
}
