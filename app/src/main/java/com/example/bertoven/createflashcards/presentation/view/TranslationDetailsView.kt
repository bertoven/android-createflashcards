package com.example.bertoven.createflashcards.presentation.view

import com.example.bertoven.createflashcards.domain.Translation

interface TranslationDetailsView {
    fun hideProgressBar()
    fun showTranslation(translation: Translation)
    fun showNoTranslation()
    fun setFabVisibility(visible: Boolean)
}