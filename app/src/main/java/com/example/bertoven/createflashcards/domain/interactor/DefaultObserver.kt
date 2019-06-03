package com.example.bertoven.createflashcards.domain.interactor

import io.reactivex.observers.DisposableSingleObserver

abstract class DefaultObserver<T> : DisposableSingleObserver<T>()