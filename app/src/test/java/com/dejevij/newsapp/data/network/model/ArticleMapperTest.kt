package com.dejevij.newsapp.data.network.model

import com.dejevij.newsapp.model.Article
import kotlin.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ArticleMapperTest {

    @Test
    fun `maps all fields`() {
        val article = articleResponse().toArticleOrNull()

        assertEquals(
            Article(
                url = "https://example.com/article",
                title = "Headline",
                sourceName = "Example News",
                author = "Jane Doe",
                description = "Description",
                content = "Content",
                imageUrl = "https://example.com/image.jpg",
                publishedAt = Instant.parse("2026-11-01T09:30:00Z"),
            ),
            article,
        )
    }

    @Test
    fun `strips the truncation marker from content`() {
        val article = articleResponse(
            content = "First 200 characters… [+3412 chars]",
        ).toArticleOrNull()

        assertEquals("First 200 characters…", article?.content)
    }

    @Test
    fun `turns blank optional fields into null`() {
        val article = articleResponse(
            author = " ",
            description = "",
            urlToImage = "",
        ).toArticleOrNull()

        assertNull(article?.author)
        assertNull(article?.description)
        assertNull(article?.imageUrl)
    }

    @Test
    fun `drops removed articles`() {
        assertNull(articleResponse(title = "[Removed]").toArticleOrNull())
    }

    @Test
    fun `drops articles with an unparseable date`() {
        assertNull(articleResponse(publishedAt = "yesterday").toArticleOrNull())
    }
}

internal fun articleResponse(
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
