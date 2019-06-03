package com.example.bertoven.createflashcards.data.entity

data class DefinitionsLexicalEntry(
    val lexicalCategory: String,
    val entries: ArrayList<Entry>
)

data class Entry(
    val senses: ArrayList<Sense>?
)

data class Sense(
    val definitions: ArrayList<String>?,
    val examples: ArrayList<Example>?
)

data class Example(
    val text: String
)