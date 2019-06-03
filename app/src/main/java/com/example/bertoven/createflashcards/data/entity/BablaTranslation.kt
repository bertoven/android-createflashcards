package com.example.bertoven.createflashcards.data.entity

data class BablaTranslation(
    val quickResultsEntries: ArrayList<QuickResultsEntry>?,
    val translationDetails: ArrayList<TranslationDetails>?,
    val synonyms: ArrayList<SynonymsEntry>?,
    val contextTranslations: ArrayList<ContextTranslation>?
)

data class QuickResultsEntry(
    val baseWord: String,
    val translations: ArrayList<String>
)

data class TranslationDetails(
    val baseWord: String,
    val senseGroups: ArrayList<SenseGroup>
)

data class SenseGroup(
    val context: String,
    val senseGroupEntries: ArrayList<SenseGroupEntry>
)

data class SenseGroupEntry(
    val baseWord: String,
    val translation: String,
    val examples: ArrayList<Pair<String, String>>
)

data class SynonymsEntry(
    val baseWord: String,
    val synonymsWords: ArrayList<String>
)

data class ContextTranslation(
    val wordWithContext: String,
    val translation: String
)