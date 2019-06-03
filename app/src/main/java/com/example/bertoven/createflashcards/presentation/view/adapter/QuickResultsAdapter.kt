package com.example.bertoven.createflashcards.presentation.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.data.entity.QuickResultsEntry
import com.example.bertoven.createflashcards.ext.getActivity
import kotlinx.android.synthetic.main.activity_translation_details.*

class QuickResultsAdapter(private var quickResults: ArrayList<QuickResultsEntry>)
    : RecyclerView.Adapter<QuickResultsAdapter.ViewHolder>() {

    private lateinit var mRecyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quick_results, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return quickResults.size
    }

    fun loadNewData(newQuickResults: ArrayList<QuickResultsEntry>) {
        quickResults = newQuickResults
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quickResultsItem = quickResults[position]

        val translationCards = mRecyclerView.parent.parent as ViewGroup
        val contentScrollView = translationCards.parent as ViewGroup

        holder.apply {
            baseWord.text = quickResultsItem.baseWord
            baseWord.setOnClickListener {
                it.context.getActivity()?.appBarLayout?.setExpanded(false, true)
                contentScrollView.scrollTo(0, translationCards.getChildAt(position + 1).top)
            }
            translations.text = quickResultsItem.translations.joinToString(", ")
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var baseWord: TextView = view.findViewById(R.id.baseWord)
        var translations: TextView = view.findViewById(R.id.translations)
    }
}