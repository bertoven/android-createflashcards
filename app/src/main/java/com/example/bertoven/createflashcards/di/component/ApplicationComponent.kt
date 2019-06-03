package com.example.bertoven.createflashcards.di.component

import android.content.Context
import com.example.bertoven.createflashcards.BaseApplication
import com.example.bertoven.createflashcards.di.module.ApplicationModule
import com.example.bertoven.createflashcards.di.qualifier.ApplicationContext
import com.example.bertoven.createflashcards.di.scope.PerApplication
import com.example.bertoven.createflashcards.domain.repository.GoogleCustomSearchRepository
import com.example.bertoven.createflashcards.domain.repository.TranslationRepository
import com.google.gson.Gson
import dagger.Component

@PerApplication
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(application: BaseApplication)

    @ApplicationContext
    fun context(): Context

    fun translationRepository(): TranslationRepository
    fun gson(): Gson
    fun googleCustomSearchRepository(): GoogleCustomSearchRepository
}