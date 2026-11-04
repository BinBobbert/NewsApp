package com.dejevij.newsapp.core.network.model

import com.dejevij.newsapp.core.model.Article
import kotlin.time.Instant

private const val REMOVED_TITLE = "[Removed]"
private val TRUNCATION_MARKER = Regex("""\s*\[\+\d+ chars]$""")

// NewsAPI keeps articles that publishers have taken down as "[Removed]" placeholders.
fun ArticleResponse.toArticleOrNull(): Article? {
    val publishedAt = runCatching { Instant.parse(publishedAt) }.getOrNull()
    if (title == REMOVED_TITLE || publishedAt == null) return null

    return Article(
        url = url,
        title = title,
        sourceName = source.name,
        author = author?.takeIf { it.isNotBlank() },
        description = description?.takeIf { it.isNotBlank() },
        content = content?.replace(TRUNCATION_MARKER, "")?.takeIf { it.isNotBlank() },
        imageUrl = urlToImage?.takeIf { it.isNotBlank() },
        publishedAt = publishedAt,
    )
}
