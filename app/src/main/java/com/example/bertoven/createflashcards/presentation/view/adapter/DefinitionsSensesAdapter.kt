package com.example.bertoven.createflashcards.presentation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.data.entity.Sense

class DefinitionsSensesAdapter(private var senses: ArrayList<Sense>)
    : RecyclerView.Adapter<DefinitionsSensesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_definitions_sense, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return senses.size
    }

    fun loadNewData(newSenses: ArrayList<Sense>) {
        senses = newSenses
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val definitionsAdapter = DefinitionsAdapter(ArrayList())
        val definitionsExamplesAdapter = DefinitionsExamplesAdapter(ArrayList())
        val senseItem = senses[position]

        holder.definitions.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = definitionsAdapter
        }

        holder.examples.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = definitionsExamplesAdapter
        }

        if (senseItem.definitions != null) {
            definitionsAdapter.loadNewData(senseItem.definitions)
        }

        if (senseItem.examples != null) {
            definitionsExamplesAdapter.loadNewData(senseItem.examples)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val definitions: RecyclerView = view.findViewById(R.id.definitions)
        val examples: RecyclerView = view.findViewById(R.id.examples)
    }
}