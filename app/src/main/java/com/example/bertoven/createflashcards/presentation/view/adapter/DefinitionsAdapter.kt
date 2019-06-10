package com.example.bertoven.createflashcards.presentation.view.adapter

import android.content.ActivityNotFoundException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.ext.createGoogleTranslateIntent

class DefinitionsAdapter(private var definitions: ArrayList<String>)
    : RecyclerView.Adapter<DefinitionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_definitions, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return definitions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = definitions[position]
        holder.definition.text = text
        val context = holder.itemView.context
        holder.itemView.setOnClickListener {
            try {
                context.startActivity(createGoogleTranslateIntent(text))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "Sorry, No Google Translation Installed",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun loadNewData(newDefinitions: ArrayList<String>) {
        definitions = newDefinitions
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val definition: TextView = view.findViewById(R.id.definition)
    }
}