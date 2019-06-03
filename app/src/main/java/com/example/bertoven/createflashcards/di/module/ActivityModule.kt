package com.example.bertoven.createflashcards.di.module

import android.app.Activity
import android.content.Context
import com.example.bertoven.createflashcards.di.qualifier.ActivityContext
import com.example.bertoven.createflashcards.di.scope.PerActivity
import com.example.bertoven.createflashcards.utils.AnkiDroidHelper
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    @PerActivity
    @ActivityContext
    internal fun provideActivityContext(): Context = activity

    @Provides
    @PerActivity
    internal fun provideAnkiDroidHelper(@ActivityContext context: Context) = AnkiDroidHelper(context)
}