package com.dejevij.newsapp.feature.articledetail

data class ArticleDetailViewData(
    val url: String,
    val title: String,
    val sourceName: String,
    val author: String?,
    val publishedAt: String,
    val description: String?,
    val content: String?,
    val imageUrl: String?,
)
