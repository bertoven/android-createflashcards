package com.example.bertoven.createflashcards.di.module

import android.content.Context
import com.example.bertoven.createflashcards.BaseApplication
import com.example.bertoven.createflashcards.data.network.GoogleCustomSearchApi
import com.example.bertoven.createflashcards.data.repository.GoogleCustomSearchDataRepository
import com.example.bertoven.createflashcards.data.repository.TranslationDataRepository
import com.example.bertoven.createflashcards.di.qualifier.ApplicationContext
import com.example.bertoven.createflashcards.di.scope.PerApplication
import com.example.bertoven.createflashcards.domain.repository.GoogleCustomSearchRepository
import com.example.bertoven.createflashcards.domain.repository.TranslationRepository
import dagger.Module
import dagger.Provides

@Module(includes = [ApiModule::class])
class ApplicationModule(private val application: BaseApplication) {

    @Provides
    @PerApplication
    internal fun provideGoogleCustomSearchRepository(googleCustomSearchApi: GoogleCustomSearchApi)
        : GoogleCustomSearchRepository = GoogleCustomSearchDataRepository(googleCustomSearchApi)

    @Provides
    @PerApplication
    @ApplicationContext
    internal fun provideApplicationContext(): Context = application

    @Provides
    @PerApplication
    internal fun provideTranslationRepository(translationDataRepository: TranslationDataRepository)
        : TranslationRepository = translationDataRepository
}