package com.example.bertoven.createflashcards.domain.interactor

import com.example.bertoven.createflashcards.data.entity.BablaTranslation
import com.example.bertoven.createflashcards.data.entity.DefinitionsLexicalEntry
import com.example.bertoven.createflashcards.domain.Translation
import com.example.bertoven.createflashcards.domain.mapper.DataMapper
import com.example.bertoven.createflashcards.domain.repository.TranslationRepository
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class GetTranslation @Inject constructor(private val translationRepository: TranslationRepository,
                                         private val dataMapper: DataMapper)
    : UseCase<Translation>() {

    override fun buildUseCaseSingle(phrase: String): Single<Translation> {
        return translationRepository.run {
            getBablaTranslation(phrase).toObservable().zipWith(getDefinitions(phrase),
                BiFunction { bablaTranslation: BablaTranslation,
                             definitions: ArrayList<DefinitionsLexicalEntry> ->

                    dataMapper.transform(phrase, bablaTranslation, definitions)
                }).singleOrError()
        }
    }
}