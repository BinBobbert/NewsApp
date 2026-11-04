package com.dejevij.newsapp.core.model

import kotlin.time.Instant

data class Article(
    val url: String,
    val title: String,
    val sourceName: String,
    val author: String?,
    val description: String?,
    val content: String?,
    val imageUrl: String?,
    val publishedAt: Instant,
)
