package com.dejevij.newsapp.feature.articlelist

data class ArticleViewData(
    val url: String,
    val title: String,
    val sourceName: String,
    val description: String?,
    val imageUrl: String?,
    val publishedAt: String,
)
