package com.dejevij.newsapp.core.database.model

import com.dejevij.newsapp.core.model.Article
import kotlin.time.Instant

fun ArticleEntity.toArticle() = Article(
    url = url,
    title = title,
    sourceName = sourceName,
    author = author,
    description = description,
    content = content,
    imageUrl = imageUrl,
    publishedAt = Instant.fromEpochMilliseconds(publishedAt),
)

fun Article.toEntity() = ArticleEntity(
    url = url,
    title = title,
    sourceName = sourceName,
    author = author,
    description = description,
    content = content,
    imageUrl = imageUrl,
    publishedAt = publishedAt.toEpochMilliseconds(),
)
