package com.example.bertoven.createflashcards.presentation.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.di.component.ActivityComponent
import com.example.bertoven.createflashcards.di.component.DaggerFragmentComponent
import com.example.bertoven.createflashcards.domain.Translation
import com.example.bertoven.createflashcards.presentation.view.activity.TRANSLATION_EXTRA
import com.example.bertoven.createflashcards.presentation.view.activity.TranslationDetailsActivity
import com.example.bertoven.createflashcards.presentation.view.adapter.ContextTranslationsAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_translation_details.*
import kotlinx.android.synthetic.main.fragment_context_translations.*
import javax.inject.Inject

class ContextTranslationsFragment : Fragment(), TranslationDetailsActivity.OnFabClickListener {

    @Inject
    lateinit var gson: Gson

    private lateinit var mViewContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = DaggerFragmentComponent.builder()
            .activityComponent(getActivityComponent())
            .build()

        component.inject(this)
    }

    private fun getActivityComponent(): ActivityComponent {
        return (activity as TranslationDetailsActivity).getActivityComponent()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_context_translations, container, false).also { mViewContext = it.context }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val translationJson: String = arguments!!.getString(TRANSLATION_EXTRA, "")
        val translation: Translation = gson.fromJson(translationJson, Translation::class.java)

        contextScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
                setFabVisibilityInActivity(shouldFabBeVisible())
            }
        )
        populateView(translation)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            setFabVisibilityInActivity(shouldFabBeVisible())
        }
    }

    private fun shouldFabBeVisible(): Boolean {
        if (contextScrollView == null) {
            return false
        }
        val view = contextScrollView.getChildAt(contextScrollView.childCount - 1) as View
        val diff = view.bottom - (contextScrollView.height + contextScrollView.scrollY)
        return diff != 0 && contextScrollView.scrollY != 0
    }

    private fun setFabVisibilityInActivity(visible: Boolean) {
        if (activity != null) {
            (activity as TranslationDetailsActivity).setFabVisibility(visible)
        }
    }

    override fun onFabClicked() {
        activity?.appBarLayout?.setExpanded(true, true)
        contextScrollView.scrollTo(0, 0)
        contextScrollView.fling(0)
    }

    private fun populateView(translation: Translation) {
        val contextTranslationsAdapter = ContextTranslationsAdapter(ArrayList())

        translation.apply {
            contextPhrase.text = if (contextTranslations != null) {
                contextRecyclerView.apply {
                    layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
                    isNestedScrollingEnabled = false
                    adapter = contextTranslationsAdapter
                }

                contextTranslationsAdapter.loadNewData(contextTranslations)

                mViewContext.getString(R.string.context_phrase, translatingPhrase)
            } else {
                getString(R.string.context_not_exists)
            }
        }
    }

    companion object {
        fun newInstance(translationJson: String): ContextTranslationsFragment {

            return ContextTranslationsFragment().apply {
                arguments = Bundle().apply {
                    putString(TRANSLATION_EXTRA, translationJson)
                }
            }
        }
    }
}