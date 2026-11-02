package com.dejevij.newsapp.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ArticleResponse(
    val source: SourceResponse,
    val author: String? = null,
    val title: String,
    val description: String? = null,
    val url: String,
    val urlToImage: String? = null,
    val publishedAt: String,
    val content: String? = null,
)
