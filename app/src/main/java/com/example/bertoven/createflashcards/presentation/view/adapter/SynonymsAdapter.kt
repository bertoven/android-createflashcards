package com.example.bertoven.createflashcards.presentation.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.data.entity.SynonymsEntry
import com.example.bertoven.createflashcards.ext.getActivity
import com.example.bertoven.createflashcards.ext.makeLinkSpan
import com.example.bertoven.createflashcards.ext.makeLinksFocusable
import com.example.bertoven.createflashcards.presentation.view.activity.ACTION_SHOW_TRANSLATION
import com.example.bertoven.createflashcards.presentation.view.activity.PHRASE_EXTRA
import com.example.bertoven.createflashcards.presentation.view.activity.TranslationDetailsActivity

class SynonymsAdapter(private var synonyms: ArrayList<SynonymsEntry>)
    : RecyclerView.Adapter<SynonymsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_synonyms, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return synonyms.size
    }

    fun loadNewData(newSynonyms: ArrayList<SynonymsEntry>) {
        synonyms = newSynonyms
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val synonymsItem = synonyms[position]

        holder.synonymsBaseWord.text = synonymsItem.baseWord

        val synonymWords = synonymsItem.synonymsWords

        holder.synonymsWords.apply {
            for (i in synonymWords.indices) {
                val clickableSpan = makeLinkSpan(synonymWords[i], View.OnClickListener {
                    val intent = Intent(context, TranslationDetailsActivity::class.java)
                    intent.action = ACTION_SHOW_TRANSLATION
                    intent.putExtra(PHRASE_EXTRA, synonymWords[i])
                    context.getActivity()?.finish()
                    context.startActivity(intent)
                })

                append(clickableSpan)
                if (i < synonymWords.size - 1) append(", ")
            }

            makeLinksFocusable(this)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val synonymsBaseWord: TextView = view.findViewById(R.id.synonymsBaseWord)
        val synonymsWords: TextView = view.findViewById(R.id.synonymsWords)
    }
}