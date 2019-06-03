package com.example.bertoven.createflashcards.presentation.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.data.entity.ContextTranslation

class ContextTranslationsAdapter(private var contextTranslations: ArrayList<ContextTranslation>)
    : RecyclerView.Adapter<ContextTranslationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_context_translations, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contextTranslations.size
    }

    fun loadNewData(newContextTranslations: ArrayList<ContextTranslation>) {
        contextTranslations = newContextTranslations
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contextTranslationsItem = contextTranslations[position]

        holder.wordWithContext.text = contextTranslationsItem.wordWithContext
        holder.contextTranslation.text = contextTranslationsItem.translation
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordWithContext: TextView = view.findViewById(R.id.wordWithContext)
        val contextTranslation: TextView = view.findViewById(R.id.contextTranslation)
    }
}