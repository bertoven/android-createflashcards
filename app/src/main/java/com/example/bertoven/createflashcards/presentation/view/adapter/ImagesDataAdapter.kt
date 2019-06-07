package com.example.bertoven.createflashcards.presentation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bertoven.createflashcards.R
import com.example.bertoven.createflashcards.data.entity.ImagesDataItem

class ImagesDataAdapter(private var imagesList: List<ImagesDataItem>) : RecyclerView.Adapter<ImagesDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(imagesList[position].link)
    }

    fun loadData(imagesList: List<ImagesDataItem>) {
        this.imagesList = imagesList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.findViewById(R.id.image)

        internal fun bindTo(imgUrl: String) {
            Glide.with(image.context)
                .load(imgUrl)
                .into(image)
        }
    }
}