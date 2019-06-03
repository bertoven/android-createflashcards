package com.example.bertoven.createflashcards.domain.interactor

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

abstract class UseCase<T> {
    private val compositeDisposable by lazy { CompositeDisposable() }

    abstract fun buildUseCaseSingle(phrase: String): Single<T>

    fun execute(observer: DefaultObserver<T>, phrase: String) {
        val single: Single<T> = buildUseCaseSingle(phrase)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        addDisposable(single.subscribeWith(observer))
    }

    private fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun dispose() {
        compositeDisposable.clear()
    }
}