package com.example.bertoven.createflashcards.presentation.view.adapter

import android.content.ActivityNotFoundException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.data.entity.ContextTranslation
import com.example.bertoven.createflashcards.ext.createGoogleTranslateIntent

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

        val baseText = contextTranslationsItem.wordWithContext
        holder.wordWithContext.text = baseText
        holder.contextTranslation.text = contextTranslationsItem.translation

        val context = holder.itemView.context
        holder.itemView.setOnClickListener {
            try {
                context.startActivity(createGoogleTranslateIntent(baseText))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "Sorry, No Google Translation Installed",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordWithContext: TextView = view.findViewById(R.id.wordWithContext)
        val contextTranslation: TextView = view.findViewById(R.id.contextTranslation)
    }
}