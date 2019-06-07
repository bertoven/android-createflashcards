package com.example.bertoven.createflashcards.domain

import com.example.bertoven.createflashcards.data.entity.*

data class Translation(
    val translatingPhrase: String,
    val quickResultsEntries: ArrayList<QuickResultsEntry>?,
    val translationDetails: ArrayList<TranslationDetails>?,
    val synonyms: ArrayList<SynonymsEntry>?,
    val contextTranslations: ArrayList<ContextTranslation>?,
    val definitions: ArrayList<DefinitionsLexicalEntry>?,
    val imagesData: ImagesData?
)