package com.example.bertoven.createflashcards.di.component

import com.example.bertoven.createflashcards.di.scope.PerFragment
import com.example.bertoven.createflashcards.presentation.view.fragment.ContextTranslationsFragment
import com.example.bertoven.createflashcards.presentation.view.fragment.DefinitionsFragment
import com.example.bertoven.createflashcards.presentation.view.fragment.TranslationDetailsFragment
import dagger.Component

@PerFragment
@Component(dependencies = [ActivityComponent::class])
interface FragmentComponent {
    fun inject(fragment: ContextTranslationsFragment)
    fun inject(fragment: DefinitionsFragment)
    fun inject(fragment: TranslationDetailsFragment)
}