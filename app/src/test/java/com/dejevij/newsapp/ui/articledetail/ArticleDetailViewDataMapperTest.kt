package com.dejevij.newsapp.ui.articledetail

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dejevij.newsapp.model.Article
import com.dejevij.newsapp.testing.FixedClock
import com.dejevij.newsapp.ui.RelativeTimeFormatter
import kotlin.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticleDetailViewDataMapperTest {

    private val mapper = ArticleDetailViewDataMapper(
        RelativeTimeFormatter(FixedClock(Instant.parse("2026-11-01T12:00:00Z"))),
    )

    @Test
    fun `maps all detail fields`() {
        val article = Article(
            url = "https://example.com/a",
            title = "Headline",
            sourceName = "BBC News",
            author = "Jane Doe",
            description = "Description",
            content = "Content",
            imageUrl = "https://example.com/a.jpg",
            publishedAt = Instant.parse("2026-11-01T10:00:00Z"),
        )

        assertEquals(
            ArticleDetailViewData(
                url = "https://example.com/a",
                title = "Headline",
                sourceName = "BBC News",
                author = "Jane Doe",
                publishedAt = "2 hours ago",
                description = "Description",
                content = "Content",
                imageUrl = "https://example.com/a.jpg",
            ),
            mapper.map(article),
        )
    }
}
