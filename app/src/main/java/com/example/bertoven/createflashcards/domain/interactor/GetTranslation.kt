package com.example.bertoven.createflashcards.domain.interactor

import com.example.bertoven.createflashcards.domain.Translation
import com.example.bertoven.createflashcards.domain.mapper.DataMapper
import com.example.bertoven.createflashcards.domain.repository.TranslationRepository
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class GetTranslation @Inject constructor(private val translationRepository: TranslationRepository,
                                         private val dataMapper: DataMapper)
    : UseCase<Translation>() {

    override fun buildUseCaseSingle(phrase: String): Single<Translation> {
        return translationRepository.run {
            Observable.zip(
                listOf(getBablaTranslation(phrase).toObservable(),
                    getDefinitions(phrase),
                    getImagesData(phrase).toObservable())
            ) {

                dataMapper.transform(phrase, it)
            }.singleOrError()

//            getBablaTranslation(phrase).toObservable().zipWith(getDefinitions(phrase),
//                BiFunction { bablaTranslation: BablaTranslation,
//                             definitions: ArrayList<DefinitionsLexicalEntry> ->
//
//                    dataMapper.transform(phrase, bablaTranslation, definitions)
//                }).singleOrError()
        }
    }
}