package com.example.bertoven.createflashcards.data.entity

data class ImagesData(
    val items: List<ImagesDataItem>
)

data class ImagesDataItem(
    val link: String,
    val image: ImageInfo
)

data class ImageInfo(
    val height: Int,
    val width: Int
)
