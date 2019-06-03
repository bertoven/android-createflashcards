package com.example.bertoven.createflashcards.di.component

import android.content.Context
import com.example.bertoven.createflashcards.di.module.ActivityModule
import com.example.bertoven.createflashcards.di.qualifier.ActivityContext
import com.example.bertoven.createflashcards.di.scope.PerActivity
import com.example.bertoven.createflashcards.domain.repository.GoogleCustomSearchRepository
import com.example.bertoven.createflashcards.domain.repository.TranslationRepository
import com.example.bertoven.createflashcards.presentation.view.activity.TranslationDetailsActivity
import com.example.bertoven.createflashcards.ext.AnkiDroidHelper
import com.google.gson.Gson
import dagger.Component

@PerActivity
@Component(modules = [ActivityModule::class], dependencies = [ApplicationComponent::class])
interface ActivityComponent {
    fun inject(activity: TranslationDetailsActivity)

    @ActivityContext
    fun context(): Context

    fun translationRepository(): TranslationRepository
    fun gson(): Gson
    fun googleCustomSearchRepository(): GoogleCustomSearchRepository
    fun ankiDroidHelper(): AnkiDroidHelper
}