package com.example.bertoven.createflashcards.presentation.view.fragment

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.graphics.Typeface
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.data.entity.ImagesData
import com.example.bertoven.createflashcards.data.entity.QuickResultsEntry
import com.example.bertoven.createflashcards.data.entity.SynonymsEntry
import com.example.bertoven.createflashcards.data.entity.TranslationDetails
import com.example.bertoven.createflashcards.di.component.ActivityComponent
import com.example.bertoven.createflashcards.di.component.DaggerFragmentComponent
import com.example.bertoven.createflashcards.domain.Translation
import com.example.bertoven.createflashcards.ext.createGoogleTranslateIntent
import com.example.bertoven.createflashcards.ext.dpToPx
import com.example.bertoven.createflashcards.ext.makeLinkSpan
import com.example.bertoven.createflashcards.ext.makeLinksFocusable
import com.example.bertoven.createflashcards.presentation.view.activity.TRANSLATION_EXTRA
import com.example.bertoven.createflashcards.presentation.view.activity.TranslationDetailsActivity
import com.example.bertoven.createflashcards.presentation.view.adapter.ImagesDataAdapter
import com.example.bertoven.createflashcards.presentation.view.adapter.QuickResultsAdapter
import com.example.bertoven.createflashcards.presentation.view.adapter.SenseGroupsAdapter
import com.example.bertoven.createflashcards.presentation.view.adapter.SynonymsAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_translation_details.*
import kotlinx.android.synthetic.main.fragment_translation_details.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class TranslationDetailsFragment : Fragment(), TranslationDetailsActivity.OnFabClickListener {

    @Inject
    lateinit var gson: Gson

    private var tts: TextToSpeech? = null

    private fun getActivityComponent(): ActivityComponent {
        return (activity as TranslationDetailsActivity).getActivityComponent()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_translation_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val component = DaggerFragmentComponent.builder()
            .activityComponent(getActivityComponent())
            .build()

        component.inject(this)

        val translationJson: String = arguments!!.getString(TRANSLATION_EXTRA, "")
        val translation = gson.fromJson(translationJson, Translation::class.java)

        detailsScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
                setFabVisibilityInActivity(shouldFabBeVisible())
            }
        )

        populateView(translation)
    }

    override fun onResume() {
        super.onResume()
        tts = TextToSpeech(requireContext(), TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                tts?.language = Locale.UK
            }
        })
    }

    override fun onPause() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onPause()
    }

    override fun onFabClicked() {
        activity?.appBarLayout?.setExpanded(true, true)
        detailsScrollView.scrollTo(0, 0)
        detailsScrollView.fling(0)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            setFabVisibilityInActivity(shouldFabBeVisible())
        }
    }

    private fun shouldFabBeVisible(): Boolean {
        if (detailsScrollView == null) {
            return false
        }
        val view = detailsScrollView.getChildAt(detailsScrollView.childCount - 1) as View
        val diff = view.bottom - (detailsScrollView.height + detailsScrollView.scrollY)
        return diff != 0 && detailsScrollView.scrollY != 0
    }

    private fun setFabVisibilityInActivity(visible: Boolean) {
        if (activity != null) {
            (activity as TranslationDetailsActivity).setFabVisibility(visible)
        }
    }

    private fun populateView(translation: Translation) {
        val dp4 = dpToPx(requireContext(), 4)
        val dp16 = dpToPx(requireContext(), 16)

        val params = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, dp4, 0, dp4) }

        translation.apply {
            detailsPhrase.text = getString(R.string.translating_phrase, translatingPhrase)
            detailsPhrase.setOnClickListener {
                try {
                    startActivity(createGoogleTranslateIntent(translatingPhrase))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        requireContext(), "Sorry, No Google Translation Installed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            createQuickResultsCard(quickResultsEntries!!, params)
            createImagesCard(imagesData!!, params, dp16)
            createTranslationsDetailsCards(translationDetails!!, params, dp16)

            if (synonyms != null) {
                createSynonymsCard(synonyms, params, dp16)
                val synonymsText = getString(R.string.synonyms_header)

                val clickableSpan = makeLinkSpan(synonymsText, View.OnClickListener {
                    val synonymsYPosition =
                        translationCards.getChildAt(translationCards.childCount - 1)
                            .top
                    activity?.appBarLayout?.setExpanded(false, true)
                    detailsScrollView.scrollTo(0, synonymsYPosition)
                    detailsScrollView.fling(0)
                })

                options.append(clickableSpan)
                makeLinksFocusable(options)
            }
        }
    }

    private fun createQuickResultsCard(
        quickResultEntries: List<QuickResultsEntry>,
        params: LayoutParams
    ) {
        val quickResultsAdapter = QuickResultsAdapter(ArrayList())

        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = quickResultsAdapter
        }

        val card = CardView(requireContext()).apply {
            layoutParams = params
            setBackgroundResource(R.drawable.quick_results_bg)
            addView(recyclerView)
        }

        translationCards.addView(card)
        quickResultsAdapter.loadNewData(quickResultEntries)
    }

    @SuppressLint("WrongConstant")
    private fun createImagesCard(imagesData: ImagesData, params: LayoutParams, padding: Int) {
        val imagesDataAdapter = ImagesDataAdapter(emptyList())

        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = FlexboxLayoutManager(context).also {
                it.flexWrap = FlexWrap.WRAP
                it.flexDirection = FlexDirection.ROW
                it.justifyContent = JustifyContent.SPACE_EVENLY
            }
            isNestedScrollingEnabled = false
            adapter = imagesDataAdapter
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        }

        val card = CardView(requireContext()).apply {
            layoutParams = params
            setContentPadding(padding, padding, padding, padding)
            addView(recyclerView)
        }

        translationCards.addView(card)
        imagesDataAdapter.loadData(imagesData.items)
    }

    private fun createTranslationsDetailsCards(
        translationDetails: ArrayList<TranslationDetails>,
        params: LayoutParams, padding: Int
    ) {

        for (translationsDetail in translationDetails) {
            val senseGroupsAdapter = SenseGroupsAdapter(ArrayList())

            val recyclerView = RecyclerView(requireContext()).apply {
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
                layoutManager = LinearLayoutManager(context)
                isNestedScrollingEnabled = false
                adapter = senseGroupsAdapter
            }

            val baseWord = translationsDetail.baseWord
            val str = SpannableStringBuilder(baseWord)
            val indexOfCurlyBracket = baseWord.indexOf("{")
            val indexOfSquareBracket = baseWord.indexOf("[")

            val index = when {
                indexOfCurlyBracket >= 0 -> indexOfCurlyBracket
                indexOfSquareBracket >= 0 -> indexOfSquareBracket
                else -> baseWord.length
            }

            str.setSpan(StyleSpan(Typeface.BOLD), 0, index - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            val linearLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )

                addView(LinearLayout(context).apply {
                    layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    )

                    addView(ImageView(context).apply {
                        layoutParams = LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.MATCH_PARENT
                        )

                        scaleType = ImageView.ScaleType.CENTER

                        setImageResource(R.drawable.ic_sound)

                        setOnClickListener {
                            val toSpeak = str.toString()
                                .substring(0, index - 1)
                            tts?.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    })

                    addView(TextView(context).apply {
                        text = str
                        textSize = 20F
                    })
                })

                addView(recyclerView)
            }

            val card = CardView(requireContext()).apply {
                layoutParams = params
                setContentPadding(padding, padding, padding, padding)
                addView(linearLayout)
            }

            translationCards.addView(card)

            senseGroupsAdapter.loadNewData(translationsDetail.senseGroups)
        }
    }

    private fun createSynonymsCard(
        synonyms: ArrayList<SynonymsEntry>,
        params: LayoutParams,
        padding: Int
    ) {
        val synonymsAdapter = SynonymsAdapter(ArrayList())

        val recyclerView = RecyclerView(requireContext()).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = synonymsAdapter
        }

        val linearLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            addView(TextView(context).apply {
                text = getString(R.string.synonyms_header)
                textSize = 20F
            })
            addView(recyclerView)
        }

        val card = CardView(requireContext()).apply {
            layoutParams = params
            setContentPadding(padding, padding, padding, padding)
            addView(linearLayout)
        }

        translationCards.addView(card)
        synonymsAdapter.loadNewData(synonyms)
    }

    companion object {
        fun newInstance(translationJson: String): TranslationDetailsFragment {

            return TranslationDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(TRANSLATION_EXTRA, translationJson)
                }
            }
        }
    }
}