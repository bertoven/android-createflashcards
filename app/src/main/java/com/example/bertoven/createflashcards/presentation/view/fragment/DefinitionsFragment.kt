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
import com.example.bertoven.createflashcards.presentation.view.adapter.DefinitionsCardsAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_translation_details.*
import kotlinx.android.synthetic.main.fragment_definitions.*
import javax.inject.Inject

class DefinitionsFragment : Fragment(), TranslationDetailsActivity.OnFabClickListener {

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
        return inflater.inflate(R.layout.fragment_definitions, container, false).also { mViewContext = it.context }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val translationJson: String = arguments!!.getString(TRANSLATION_EXTRA, "")
        val translation: Translation = gson.fromJson(translationJson, Translation::class.java)

        definitionsScrollView.setOnScrollChangeListener(
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
        if (definitionsScrollView == null) {
            return false
        }
        val view = definitionsScrollView.getChildAt(definitionsScrollView.childCount - 1) as View
        val diff = view.bottom - (definitionsScrollView.height + definitionsScrollView.scrollY)
        return diff != 0 && definitionsScrollView.scrollY != 0
    }

    private fun setFabVisibilityInActivity(visible: Boolean) {
        if (activity != null) {
            (activity as TranslationDetailsActivity).setFabVisibility(visible)
        }
    }

    override fun onFabClicked() {
        activity?.appBarLayout?.setExpanded(true, true)
        definitionsScrollView.scrollTo(0, 0)
        definitionsScrollView.fling(0)
    }

    private fun populateView(translation: Translation) {
        val definitionsAdapter = DefinitionsCardsAdapter(ArrayList())

        translation.apply {
            definitionsPhrase.text = mViewContext.getString(R.string.definitions_phrase, translatingPhrase)

            definitionsRecyclerView.apply {
                layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
                isNestedScrollingEnabled = false
                adapter = definitionsAdapter
            }

            definitionsAdapter.loadNewData(definitions!!)
        }
    }

    companion object {
        fun newInstance(translationJson: String): DefinitionsFragment {

            return DefinitionsFragment().apply {
                arguments = Bundle().apply {
                    putString(TRANSLATION_EXTRA, translationJson)
                }
            }
        }
    }
}