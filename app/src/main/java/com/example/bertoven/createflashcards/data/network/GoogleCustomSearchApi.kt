package com.example.bertoven.createflashcards.data.network

import com.example.bertoven.createflashcards.data.entity.ImagesData
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface GoogleCustomSearchApi {
    @GET
    fun getImages(
        @Url url: String
    ): Single<ImagesData>
}