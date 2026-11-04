package com.dejevij.newsapp.core.testing

import com.dejevij.newsapp.core.network.model.ArticleResponse
import com.dejevij.newsapp.core.network.model.SourceResponse

fun articleResponse(
    url: String = "https://example.com/article",
    title: String = "Headline",
    author: String? = "Jane Doe",
    description: String? = "Description",
    content: String? = "Content",
    urlToImage: String? = "https://example.com/image.jpg",
    publishedAt: String = "2026-11-01T09:30:00Z",
) = ArticleResponse(
    source = SourceResponse(id = "example", name = "Example News"),
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content,
)
