package com.example.bertoven.createflashcards.data.entity

data class InflectionsLexicalEntry(
    val lexicalCategory: String,
    val inflectionOf: ArrayList<Inflection>
)

data class Inflection(
    val id: String,
    val text: String
)