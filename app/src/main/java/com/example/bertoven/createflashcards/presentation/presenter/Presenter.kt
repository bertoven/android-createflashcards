package com.example.bertoven.createflashcards.presentation.presenter

interface Presenter {
    fun getTranslationData(phrase: String)
    fun unsubscribe()
    fun getImagesData(url: String)
}