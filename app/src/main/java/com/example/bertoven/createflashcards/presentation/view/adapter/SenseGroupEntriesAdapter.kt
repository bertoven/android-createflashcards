package com.example.bertoven.createflashcards.presentation.view.adapter

import android.content.Intent
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.data.entity.SenseGroupEntry
import com.example.bertoven.createflashcards.presentation.view.activity.ACTION_SHOW_TRANSLATION
import com.example.bertoven.createflashcards.presentation.view.activity.PHRASE_EXTRA
import com.example.bertoven.createflashcards.presentation.view.activity.TranslationDetailsActivity
import com.example.bertoven.createflashcards.utils.*

class SenseGroupEntriesAdapter(private var senseGroupEntries: ArrayList<SenseGroupEntry>)
    : RecyclerView.Adapter<SenseGroupEntriesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sense_group_entry, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return senseGroupEntries.size
    }

    fun loadNewData(newSenseGroupEntries: ArrayList<SenseGroupEntry>) {
        senseGroupEntries = newSenseGroupEntries
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val senseGroupEntryItem = senseGroupEntries[position]

        val phrase = senseGroupEntryItem.baseWord
        val indexOfBracket = phrase.indexOf("(")

        holder.apply {
            if (senseGroupEntryItem.examples.size > 0) {
                expand.setImageResource(R.drawable.ic_show_more)

                view.setOnClickListener {
                    when (examples.visibility) {
                        View.GONE -> {
                            expand.setImageResource(R.drawable.ic_show_less)
                            expandView(examples)
                        }
                        View.VISIBLE -> {
                            expand.setImageResource(R.drawable.ic_show_more)
                            collapseView(examples)
                        }
                    }
                }
            }

            if (indexOfBracket >= 0) {
                val baseString = phrase.substring(0, indexOfBracket + 6)
                val alsoSubStr = phrase.substring(indexOfBracket + 6, phrase.length - 1)
                val alsoStrings = alsoSubStr.split(", ")

                baseWord.append(baseString)

                for (i in alsoStrings.indices) {
                    val clickableSpan = makeLinkSpan(alsoStrings[i], View.OnClickListener {
                        val intent = Intent(it.context, TranslationDetailsActivity::class.java)
                        intent.action = ACTION_SHOW_TRANSLATION
                        intent.putExtra(PHRASE_EXTRA, alsoStrings[i])
                        it.context.getActivity()?.finish()
                        it.context.startActivity(intent)
                    })

                    baseWord.append(clickableSpan)
                    if (i < alsoStrings.size - 1) baseWord.append(", ")
                }

                baseWord.append(")")
                makeLinksFocusable(baseWord)
            } else {
                baseWord.text = phrase
            }

            translation.text = senseGroupEntryItem.translation
            examples.visibility = View.GONE
        }

        val examplesContext = holder.examples.context

        val dp4 = dpToPx(examplesContext, 4)
        val dp8 = dpToPx(examplesContext, 8)
        val dp16 = dpToPx(examplesContext, 16)

        for (example in senseGroupEntryItem.examples) {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, dp4, 0, dp4)
            }

            val exampleLinearLayout = LinearLayout(examplesContext).apply {
                layoutParams = params
                weightSum = 2F
                setBackgroundColor(ContextCompat.getColor(examplesContext, R.color.background_light))

                addView(TextView(examplesContext).apply {
                    text = example.first
                    setPadding(dp8, dp8, dp16, dp8)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD
                    }

                    layoutParams = TableLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.MATCH_PARENT, 1F
                    )
                })

                addView(TextView(examplesContext).apply {
                    text = example.second
                    setPadding(dp4, dp8, dp8, dp8)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD
                    }

                    layoutParams = TableLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.MATCH_PARENT, 1F
                    )
                })
            }

            holder.examples.addView(exampleLinearLayout)
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var baseWord: TextView = view.findViewById(R.id.sgeBaseWord)
        var translation: TextView = view.findViewById(R.id.sgeTranslation)
        var expand: ImageView = view.findViewById(R.id.expand)
        var examples: LinearLayout = view.findViewById(R.id.sgeExamples)
    }
}