package com.example.bertoven.createflashcards.data.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface BablaApi {
    @GET("{phrase}")
    fun getBablaRawData(@Path("phrase") phrase: String): Single<String>
}