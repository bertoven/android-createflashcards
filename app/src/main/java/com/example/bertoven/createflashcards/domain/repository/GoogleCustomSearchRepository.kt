package com.example.bertoven.createflashcards.domain.repository

import com.example.bertoven.createflashcards.data.entity.ImagesData
import io.reactivex.Single

interface GoogleCustomSearchRepository {
    fun getImagesData(url: String): Single<ImagesData>
}