package com.example.bertoven.createflashcards.presentation.presenter

import com.example.bertoven.createflashcards.di.scope.PerActivity
import com.example.bertoven.createflashcards.domain.Translation
import com.example.bertoven.createflashcards.domain.interactor.DefaultObserver
import com.example.bertoven.createflashcards.domain.interactor.GetTranslation
import com.example.bertoven.createflashcards.domain.repository.GoogleCustomSearchRepository
import com.example.bertoven.createflashcards.presentation.view.TranslationDetailsView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@PerActivity
class TranslationDetailsPresenter @Inject constructor(
    private val getTranslationUseCase: GetTranslation,
    private val googleCustomSearchRepository: GoogleCustomSearchRepository
) : Presenter {

    private val disposables = CompositeDisposable()

    override fun getImagesData(url: String) {
        disposables += googleCustomSearchRepository.getImagesData(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    translationDetailsView.onGetImagesDataSuccess(it)
                },
                onError = {
                    Timber.e(it)
                    translationDetailsView.onGetImagesDataError(it)
                }
            )
    }

    private lateinit var translationDetailsView: TranslationDetailsView

    fun setView(view: TranslationDetailsView) {
        translationDetailsView = view
    }

    override fun getTranslationData(phrase: String) {
        getTranslationUseCase.execute(TranslationObserver(), phrase)
    }

    override fun unsubscribe() {
        getTranslationUseCase.dispose()
        disposables.clear()
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