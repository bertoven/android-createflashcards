package com.example.bertoven.createflashcards.domain.repository

import com.example.bertoven.createflashcards.data.entity.BablaTranslation
import com.example.bertoven.createflashcards.data.entity.DefinitionsLexicalEntry
import com.example.bertoven.createflashcards.data.entity.ImagesData
import io.reactivex.Observable
import io.reactivex.Single

interface TranslationRepository {
    fun getBablaTranslation(phrase: String): Single<BablaTranslation>
    fun getDefinitions(phrase: String): Observable<ArrayList<DefinitionsLexicalEntry>>
    fun getImagesData(phrase: String): Single<ImagesData>
}