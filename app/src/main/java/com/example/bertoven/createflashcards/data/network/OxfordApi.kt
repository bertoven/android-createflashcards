package com.example.bertoven.createflashcards.data.network

import com.example.bertoven.createflashcards.data.entity.DefinitionsLexicalEntry
import com.example.bertoven.createflashcards.data.entity.InflectionsLexicalEntry
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface OxfordApi {
    @Headers("Accept: application/json")
    @GET("inflections/{language}/{phrase}")
    fun getInflections(
        @Header("app_id") appId: String,
        @Header("app_key") appKey: String,
        @Path("language") language: String,
        @Path("phrase") phrase: String
    ): Single<ArrayList<InflectionsLexicalEntry>>


    @Headers("Accept: application/json")
    @GET("entries/{language}/{inflectionId}")
    fun getDefinitions(
        @Header("app_id") appId: String,
        @Header("app_key") appKey: String,
        @Path("language") language: String,
        @Path("inflectionId") inflectionId: String
    ): Single<ArrayList<DefinitionsLexicalEntry>>
}