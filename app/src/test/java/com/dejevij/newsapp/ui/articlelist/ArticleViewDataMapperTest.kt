package com.dejevij.newsapp.ui.articlelist

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dejevij.newsapp.model.Article
import com.dejevij.newsapp.testing.FixedClock
import com.dejevij.newsapp.ui.RelativeTimeFormatter
import kotlin.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticleViewDataMapperTest {

    private val now = Instant.parse("2026-11-01T12:00:00Z")
    private val mapper = ArticleViewDataMapper(RelativeTimeFormatter(FixedClock(now)))

    @Test
    fun `maps display fields`() {
        assertEquals(
            ArticleViewData(
                url = "https://example.com/a",
                title = "Headline",
                sourceName = "BBC News",
                description = "Description",
                imageUrl = "https://example.com/a.jpg",
                publishedAt = "2 hours ago",
            ),
            mapper.map(article(publishedAt = Instant.parse("2026-11-01T10:00:00Z"))),
        )
    }

    @Test
    fun `formats publishedAt relative to the clock`() {
        val fiveMinutesAgo = article(publishedAt = Instant.parse("2026-11-01T11:55:00Z"))

        assertEquals("5 minutes ago", mapper.map(fiveMinutesAgo).publishedAt)
    }

    private fun article(publishedAt: Instant) = Article(
        url = "https://example.com/a",
        title = "Headline",
        sourceName = "BBC News",
        author = null,
        description = "Description",
        content = null,
        imageUrl = "https://example.com/a.jpg",
        publishedAt = publishedAt,
    )
}
