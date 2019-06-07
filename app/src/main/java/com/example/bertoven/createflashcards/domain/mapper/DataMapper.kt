package com.example.bertoven.createflashcards.domain.mapper

import com.example.bertoven.createflashcards.data.entity.BablaTranslation
import com.example.bertoven.createflashcards.data.entity.DefinitionsLexicalEntry
import com.example.bertoven.createflashcards.data.entity.ImagesData
import com.example.bertoven.createflashcards.domain.Translation
import javax.inject.Inject

class DataMapper @Inject constructor() {

    fun transform(phrase: String, data: Array<Any>): Translation {
        if (data.size < 3) {
            throw IllegalStateException("Some translation data not available")
        }
        val bablaTranslation = data[0] as BablaTranslation
        val definitions = data[1] as ArrayList<DefinitionsLexicalEntry>
        val imagesData = data[2] as ImagesData
        return Translation(
            phrase,
            bablaTranslation.quickResultsEntries,
            bablaTranslation.translationDetails,
            bablaTranslation.synonyms,
            bablaTranslation.contextTranslations,
            if (definitions.isNotEmpty()) definitions else null,
            imagesData
        )
    }
}