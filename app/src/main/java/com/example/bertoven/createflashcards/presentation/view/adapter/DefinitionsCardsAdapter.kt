package com.example.bertoven.createflashcards.presentation.view.adapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.data.entity.DefinitionsLexicalEntry
import com.example.bertoven.createflashcards.data.entity.Sense

class DefinitionsCardsAdapter(private var definitions: ArrayList<DefinitionsLexicalEntry>)
    : RecyclerView.Adapter<DefinitionsCardsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_definitions_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return definitions.size
    }

    fun loadNewData(newDefinitions: ArrayList<DefinitionsLexicalEntry>) {
        definitions = newDefinitions
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val definitionsSensesAdapter = DefinitionsSensesAdapter(ArrayList())
        val definitionItem = definitions[position]

        holder.category.text = definitionItem.lexicalCategory
        holder.senses.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = definitionsSensesAdapter
        }

        val senses = ArrayList<Sense>()
        for (entry in definitionItem.entries) {
            if (entry.senses != null) {
                senses.addAll(entry.senses)
            }
        }

        definitionsSensesAdapter.loadNewData(senses)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val category: TextView = view.findViewById(R.id.lexicalCategory)
        val senses: RecyclerView = view.findViewById(R.id.sensesRecyclerView)
    }
}