package com.example.bertoven.createflashcards.data.repository

import com.example.bertoven.createflashcards.data.entity.ImagesData
import com.example.bertoven.createflashcards.data.network.GoogleCustomSearchApi
import com.example.bertoven.createflashcards.di.scope.PerApplication
import com.example.bertoven.createflashcards.domain.repository.GoogleCustomSearchRepository
import io.reactivex.Single
import javax.inject.Inject

@PerApplication
class GoogleCustomSearchDataRepository @Inject constructor(
    private val googleCustomSearchApi: GoogleCustomSearchApi
) : GoogleCustomSearchRepository {

    override fun getImagesData(url: String): Single<ImagesData> {
        return googleCustomSearchApi.getImages(url)
    }
}