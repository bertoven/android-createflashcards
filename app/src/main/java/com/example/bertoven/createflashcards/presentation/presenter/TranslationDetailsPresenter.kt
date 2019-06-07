package com.example.bertoven.createflashcards.presentation.presenter

import com.example.bertoven.createflashcards.di.scope.PerActivity
import com.example.bertoven.createflashcards.domain.Translation
import com.example.bertoven.createflashcards.domain.interactor.DefaultObserver
import com.example.bertoven.createflashcards.domain.interactor.GetTranslation
import com.example.bertoven.createflashcards.presentation.view.TranslationDetailsView
import javax.inject.Inject

@PerActivity
class TranslationDetailsPresenter @Inject constructor(
    private val getTranslationUseCase: GetTranslation
) : Presenter {

    private lateinit var translationDetailsView: TranslationDetailsView

    fun setView(view: TranslationDetailsView) {
        translationDetailsView = view
    }

    override fun getTranslationData(phrase: String) {
        getTranslationUseCase.execute(TranslationObserver(), phrase)
    }

    override fun unsubscribe() {
        getTranslationUseCase.dispose()
    }

    private fun showTranslationInView(translation: Translation) {
        translationDetailsView.showTranslation(translation)
    }

    private fun showNoTranslationInView() {
        translationDetailsView.showNoTranslation()
    }

    inner class TranslationObserver : DefaultObserver<Translation>() {
        override fun onSuccess(translation: Translation) {
            showTranslationInView(translation)
        }

        override fun onError(e: Throwable) {
            showNoTranslationInView()
        }
    }
}