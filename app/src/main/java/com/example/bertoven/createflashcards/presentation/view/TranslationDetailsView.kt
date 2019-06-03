package com.example.bertoven.createflashcards.presentation.view

import com.example.bertoven.createflashcards.data.entity.ImagesData
import com.example.bertoven.createflashcards.domain.Translation

interface TranslationDetailsView {
    fun hideProgressBar()
    fun showTranslation(translation: Translation)
    fun showNoTranslation()
    fun onGetImagesDataSuccess(imagesData: ImagesData)
    fun onGetImagesDataError(ex: Throwable)
}