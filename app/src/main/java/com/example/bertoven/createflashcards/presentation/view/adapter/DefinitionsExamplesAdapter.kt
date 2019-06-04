package com.example.bertoven.createflashcards.presentation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.data.entity.Example

class DefinitionsExamplesAdapter(private var examples: ArrayList<Example>)
    : RecyclerView.Adapter<DefinitionsExamplesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_definitions_example, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return examples.size
    }

    fun loadNewData(newExamples: ArrayList<Example>) {
        examples = newExamples
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.example.text = examples[position].text
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val example: TextView = view.findViewById(R.id.example)
    }
}