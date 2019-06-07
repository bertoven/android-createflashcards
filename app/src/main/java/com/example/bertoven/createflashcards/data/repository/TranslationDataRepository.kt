package com.example.bertoven.createflashcards.data.repository

import com.example.bertoven.createflashcards.BuildConfig
import com.example.bertoven.createflashcards.data.entity.BablaTranslation
import com.example.bertoven.createflashcards.data.entity.DefinitionsLexicalEntry
import com.example.bertoven.createflashcards.data.entity.ImagesData
import com.example.bertoven.createflashcards.data.entity.InflectionsLexicalEntry
import com.example.bertoven.createflashcards.data.entity.mapper.BablaDataParser
import com.example.bertoven.createflashcards.data.entity.mapper.InflectionsMapper
import com.example.bertoven.createflashcards.data.network.BablaApi
import com.example.bertoven.createflashcards.data.network.GoogleCustomSearchApi
import com.example.bertoven.createflashcards.data.network.OxfordApi
import com.example.bertoven.createflashcards.di.scope.PerApplication
import com.example.bertoven.createflashcards.domain.repository.TranslationRepository
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

internal const val DEFAULT_LANGUAGE = "en"

@PerApplication
class TranslationDataRepository @Inject constructor(
    private val bablaApi: BablaApi,
    private val oxfordApi: OxfordApi,
    private val googleCustomSearchApi: GoogleCustomSearchApi,
    private val bablaDataParser: BablaDataParser,
    private val inflectionsMapper: InflectionsMapper
) : TranslationRepository {

    override fun getBablaTranslation(phrase: String): Single<BablaTranslation> {
        val urlPathSuffix = phrase.replace(" ", "-")

        return bablaApi.getBablaRawData(urlPathSuffix).flatMap { bablaDataParser.parse(it) }
    }

    override fun getDefinitions(phrase: String): Observable<ArrayList<DefinitionsLexicalEntry>> {
        return inflectionsMapper.transform(getInflections(phrase))
    }

    override fun getImagesData(phrase: String): Single<ImagesData> {
        val apiKey = BuildConfig.GOOGLE_API_KEY
        val cx = BuildConfig.SEARCH_ENGINE_ID
        val urlString = "https://www.googleapis.com/customsearch/v1?q=$phrase&key=$apiKey&cx=$cx&alt=json&searchType=image"
        return googleCustomSearchApi.getImages(urlString)
    }

    private fun getInflections(phrase: String): Single<ArrayList<InflectionsLexicalEntry>> {
        return oxfordApi.getInflections(BuildConfig.OXF_APP_ID, BuildConfig.OXF_APP_KEY, DEFAULT_LANGUAGE, phrase)
    }
}