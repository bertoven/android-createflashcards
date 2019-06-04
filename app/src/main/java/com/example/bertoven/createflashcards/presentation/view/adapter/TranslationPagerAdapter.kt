package com.example.bertoven.createflashcards.presentation.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
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
    var currentFragment: Fragment? = null
        private set

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
                        DefinitionsFragment.newInstance(translationJson) as Fragment
                    } else {
                        ContextTranslationsFragment.newInstance(translationJson) as Fragment
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

    override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any) {
        if (currentFragment !== obj) {
            currentFragment = obj as Fragment
        }
        super.setPrimaryItem(container, position, obj)
    }

    fun getTabView(context: Context, position: Int): View {
        val tab = LayoutInflater.from(context).inflate(R.layout.custom_tab, null)
        val tv = tab.findViewById(R.id.custom_text) as TextView
        tv.text = getPageTitle(position)
        return tab
    }
}