package com.dejevij.newsapp.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class TopHeadlinesResponse(
    val status: String,
    val totalResults: Int = 0,
    val articles: List<ArticleResponse> = emptyList(),
)
