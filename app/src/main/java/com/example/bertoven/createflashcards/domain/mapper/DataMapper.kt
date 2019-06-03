package com.example.bertoven.createflashcards.domain.mapper

import com.example.bertoven.createflashcards.data.entity.BablaTranslation
import com.example.bertoven.createflashcards.data.entity.DefinitionsLexicalEntry
import com.example.bertoven.createflashcards.domain.Translation
import javax.inject.Inject

class DataMapper @Inject constructor() {

    fun transform(phrase: String,
                  translation: BablaTranslation,
                  definitions: ArrayList<DefinitionsLexicalEntry>): Translation =
        translation.run {
            Translation(phrase,
                quickResultsEntries,
                translationDetails,
                synonyms,
                contextTranslations,
                if (definitions.isNotEmpty()) definitions else null)
        }
}