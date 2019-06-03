package com.example.bertoven.createflashcards.presentation.view.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.domain.Translation
import com.example.bertoven.createflashcards.presentation.view.fragment.ContextTranslationsFragment
import com.example.bertoven.createflashcards.presentation.view.fragment.DefinitionsFragment
import com.example.bertoven.createflashcards.presentation.view.fragment.TranslationDetailsFragment
import com.google.gson.Gson

class TranslationPagerAdapter(private val mContext: Context,
                              private val itemCount: Int,
                              private val translation: Translation,
                              fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val gson = Gson()

    override fun getItem(position: Int): Fragment {
        val translationJson = gson.toJson(translation)

        return when (itemCount) {
            3 -> when (position) {
                0 -> TranslationDetailsFragment.newInstance(translationJson)
                1 -> DefinitionsFragment.newInstance(translationJson)
                2 -> ContextTranslationsFragment.newInstance(translationJson)
                else -> TranslationDetailsFragment.newInstance(translationJson)
            }
            2 -> when (position) {
                0 -> TranslationDetailsFragment.newInstance(translationJson)
                1 -> {
                    if (translation.definitions != null) {
                        DefinitionsFragment.newInstance(translationJson)
                    } else {
                        ContextTranslationsFragment.newInstance(translationJson)
                    }
                }
                else -> TranslationDetailsFragment.newInstance(translationJson)
            }
            else -> {
                if (translation.quickResultsEntries != null) {
                    TranslationDetailsFragment.newInstance(translationJson)
                } else {
                    ContextTranslationsFragment.newInstance(translationJson)
                }
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val phrase = translation.translatingPhrase

        return when (itemCount) {
            3 -> when (position) {
                0 -> "\"$phrase\""
                1 -> mContext.getString(R.string.definitions_tab)
                2 -> mContext.getString(R.string.context_tab)
                else -> "\"$phrase\""
            }
            2 -> when (position) {
                0 -> "\"$phrase\""
                1 -> {
                    if (translation.definitions != null) {
                        mContext.getString(R.string.definitions_tab)
                    } else {
                        mContext.getString(R.string.context_tab)
                    }
                }
                else -> "\"$phrase\""
            }
            else -> {
                if (translation.quickResultsEntries != null) {
                    "\"$phrase\""
                } else {
                    mContext.getString(R.string.context_tab)
                }
            }
        }
    }

    override fun getCount(): Int {
        return itemCount
    }
}