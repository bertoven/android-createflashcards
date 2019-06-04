package com.example.bertoven.createflashcards.presentation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bertoven.createflashcards.R

class DefinitionsAdapter(private var definitions: ArrayList<String>)
    : RecyclerView.Adapter<DefinitionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_definitions, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return definitions.size
    }

    fun loadNewData(newDefinitions: ArrayList<String>) {
        definitions = newDefinitions
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.definition.text = definitions[position]
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val definition: TextView = view.findViewById(R.id.definition)
    }
}