package com.dejevij.newsapp.testing

import com.dejevij.newsapp.core.model.Article
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

val otherTestArticle = testArticle.copy(
    url = "https://example.com/other-test-article",
    title = "Other test headline",
    description = "Other test description",
)
