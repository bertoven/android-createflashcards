package com.example.bertoven.createflashcards.presentation.presenter

interface Presenter {
    fun getTranslationData(phrase: String)
    fun unsubscribe()
}