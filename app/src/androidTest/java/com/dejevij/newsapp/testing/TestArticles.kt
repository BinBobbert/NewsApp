package com.dejevij.newsapp.testing

import com.dejevij.newsapp.model.Article
import kotlin.time.Clock

val testArticle = Article(
    url = "https://example.com/test-article",
    title = "Test headline",
    sourceName = "Test News",
    author = "Jane Doe",
    description = "Test description",
    content = "Test content",
    imageUrl = null,
    publishedAt = Clock.System.now(),
)
