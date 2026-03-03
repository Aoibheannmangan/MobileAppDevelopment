package com.example.madproject

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Word(
    @SerialName("word_text") val wordText: String,
    @SerialName("word_meaning") val wordMeaning: String,
    @SerialName("book_found_in") val bookFoundIn: String
)