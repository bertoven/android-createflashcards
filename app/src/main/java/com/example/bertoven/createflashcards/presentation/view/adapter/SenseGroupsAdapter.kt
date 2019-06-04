package com.example.bertoven.createflashcards.presentation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.data.entity.SenseGroup

class SenseGroupsAdapter(private var senseGroups: ArrayList<SenseGroup>)
    : RecyclerView.Adapter<SenseGroupsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sense_groups, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return senseGroups.size
    }

    fun loadNewData(newSenseGroups: ArrayList<SenseGroup>) {
        senseGroups = newSenseGroups
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val senseGroupEntriesAdapter = SenseGroupEntriesAdapter(ArrayList())
        val senseGroupItem = senseGroups[position]

        if (senseGroupItem.context.isNotEmpty()) {
            holder.senseGroupContext.visibility = View.VISIBLE
            holder.senseGroupContext.text = senseGroupItem.context
        }

        holder.recyclerView.apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutManager = LinearLayoutManager(holder.recyclerView.context)
            isNestedScrollingEnabled = false
            adapter = senseGroupEntriesAdapter
            addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation))
        }

        senseGroupEntriesAdapter.loadNewData(senseGroupItem.senseGroupEntries)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var senseGroupContext: TextView = view.findViewById(R.id.examplesContext)
        var recyclerView: RecyclerView = view.findViewById(R.id.examplesRecyclerView)
    }
}