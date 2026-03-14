package com.example.madproject.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supabase DTO — used only for network calls to the words table.
 * Exists for when we make calls to supabase, has no ID and things because supabase generates them
 */
@Serializable
data class Word(
    @SerialName("word_text") val wordText: String,
    @SerialName("word_meaning") val wordMeaning: String,
    @SerialName("book_found_in") val bookFoundIn: String
)
