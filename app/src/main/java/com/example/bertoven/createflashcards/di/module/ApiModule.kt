package com.example.bertoven.createflashcards.di.module

import com.example.bertoven.createflashcards.data.network.BablaApi
import com.example.bertoven.createflashcards.data.network.GoogleCustomSearchApi
import com.example.bertoven.createflashcards.data.network.OxfordApi
import com.example.bertoven.createflashcards.di.qualifier.BablaRetrofit
import com.example.bertoven.createflashcards.di.qualifier.OxfordRetrofit
import com.example.bertoven.createflashcards.di.scope.PerApplication
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module(includes = [NetworkModule::class])
class ApiModule {

    @Provides
    @PerApplication
    internal fun provideBablaApi(@BablaRetrofit retrofit: Retrofit): BablaApi =
        retrofit.create(BablaApi::class.java)

    @Provides
    @PerApplication
    internal fun provideOxfordApi(@OxfordRetrofit retrofit: Retrofit): OxfordApi =
        retrofit.create(OxfordApi::class.java)

    @Provides
    @PerApplication
    internal fun provideGoogleCustomSearchApi(@OxfordRetrofit retrofit: Retrofit): GoogleCustomSearchApi =
        retrofit.create(GoogleCustomSearchApi::class.java)
}