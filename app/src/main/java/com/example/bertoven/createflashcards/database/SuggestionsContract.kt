package com.example.bertoven.createflashcards.database

object SuggestionsContract {
    internal const val TABLE_NAME = "SuggestionEntries"

    object Columns {
        const val TEXT_LENGTH = "text_length"
        const val MATCHES_LENGTH = "matches_length"
        const val WORD_ID = "word_id"
    }
}