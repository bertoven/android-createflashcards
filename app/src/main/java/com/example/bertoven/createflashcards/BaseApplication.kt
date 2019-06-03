package com.example.bertoven.createflashcards

import android.support.multidex.MultiDexApplication
import com.example.bertoven.createflashcards.di.component.ApplicationComponent
import com.example.bertoven.createflashcards.di.component.DaggerApplicationComponent
import com.example.bertoven.createflashcards.di.module.ApplicationModule
import com.example.bertoven.createflashcards.ext.TimberDebugTreeImpl
import timber.log.Timber
import javax.inject.Inject

class BaseApplication @Inject constructor() : MultiDexApplication() {

    private lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(TimberDebugTreeImpl())
        }

        initializeInjector()
    }

    fun getApplicationComponent() = applicationComponent

    private fun initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }
}