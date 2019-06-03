package com.example.bertoven.createflashcards.data.entity.mapper

import com.example.bertoven.createflashcards.BuildConfig
import com.example.bertoven.createflashcards.data.entity.DefinitionsLexicalEntry
import com.example.bertoven.createflashcards.data.entity.InflectionsLexicalEntry
import com.example.bertoven.createflashcards.data.network.OxfordApi
import com.example.bertoven.createflashcards.data.repository.DEFAULT_LANGUAGE
import com.example.bertoven.createflashcards.di.scope.PerApplication
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

@PerApplication
class InflectionsMapper @Inject constructor(private val oxfordApi: OxfordApi) {

    fun transform(inflections: Single<ArrayList<InflectionsLexicalEntry>>): Observable<ArrayList<DefinitionsLexicalEntry>> {

        return inflections.map {
            val inflectionsIds = ArrayList<String>()
            for (inflectionsLexicalEntry in it) {
                for (inflection in inflectionsLexicalEntry.inflectionOf) {
                    inflectionsIds.add(inflection.id)
                }
            }
            inflectionsIds.distinct()
        }
                .flatMapObservable { inflectionsIds ->
                    Observable.fromIterable(inflectionsIds)
                }
                .flatMapSingle { inflectionId ->
                    oxfordApi.getDefinitions(BuildConfig.OXF_APP_ID, BuildConfig.OXF_APP_KEY, DEFAULT_LANGUAGE, inflectionId)
                }
                .onErrorReturn { ArrayList() }
    }
}