package com.dejevij.newsapp.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dejevij.newsapp.core.database.NewsDatabase
import com.dejevij.newsapp.core.database.model.ArticleEntity
import com.dejevij.newsapp.core.database.model.HeadlineQueryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeadlineDaoTest {

    private val database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext<Context>(),
        NewsDatabase::class.java,
    ).allowMainThreadQueries().build()
    private val dao = database.headlineDao()

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `headlines are filtered by country and category`() = runTest {
        dao.replaceHeadlines("us", mapOf("sports" to listOf(article("a"))), fetchedAt = 0)
        dao.replaceHeadlines("us", mapOf("health" to listOf(article("b"))), fetchedAt = 0)
        dao.replaceHeadlines("gb", mapOf("sports" to listOf(article("c"))), fetchedAt = 0)

        assertEquals(listOf("a"), urls("us", "sports"))
    }

    @Test
    fun `headlines across categories are distinct and newest first`() = runTest {
        dao.replaceHeadlines(
            "us",
            mapOf(
                "sports" to listOf(article("old", publishedAt = 1), article("shared", 2)),
                "health" to listOf(article("new", publishedAt = 3), article("shared", 2)),
            ),
            fetchedAt = 0,
        )

        assertEquals(listOf("new", "shared", "old"), urls("us", "sports", "health"))
    }

    @Test
    fun `headlines published at the same time keep the api order`() = runTest {
        dao.replaceHeadlines(
            "us",
            mapOf("all" to listOf(article("b"), article("a"))),
            fetchedAt = 0,
        )

        assertEquals(listOf("b", "a"), urls("us", "all"))
    }

    @Test
    fun `replacing a category removes its old headlines and orphaned articles`() = runTest {
        dao.replaceHeadlines(
            "us",
            mapOf("all" to listOf(article("a"), article("b"))),
            fetchedAt = 0,
        )

        dao.replaceHeadlines("us", mapOf("all" to listOf(article("b"))), fetchedAt = 0)

        assertEquals(listOf("b"), urls("us", "all"))
        assertNull(dao.observeArticle("a").first())
    }

    @Test
    fun `replacing a category keeps articles other queries still refer to`() = runTest {
        dao.replaceHeadlines("us", mapOf("all" to listOf(article("a"))), fetchedAt = 0)
        dao.replaceHeadlines("gb", mapOf("all" to listOf(article("a"))), fetchedAt = 0)

        dao.replaceHeadlines("us", mapOf("all" to emptyList()), fetchedAt = 0)

        assertEquals("a", dao.observeArticle("a").first()?.url)
    }

    @Test
    fun `replacing headlines updates the article and the query fetch time`() = runTest {
        dao.replaceHeadlines("us", mapOf("all" to listOf(article("a", title = "Old"))), 1)

        dao.replaceHeadlines("us", mapOf("all" to listOf(article("a", title = "New"))), 2)

        assertEquals("New", dao.observeArticle("a").first()?.title)
        assertEquals(
            listOf(HeadlineQueryEntity("us", "all", 2)),
            dao.getQueries("us", listOf("all")),
        )
    }

    private suspend fun urls(country: String, vararg categories: String) =
        dao.observeHeadlines(country, categories.toList()).first().map { it.url }

    private fun article(url: String, publishedAt: Long = 0, title: String = "Headline") =
        ArticleEntity(
            url = url,
            title = title,
            sourceName = "BBC News",
            author = null,
            description = null,
            content = null,
            imageUrl = null,
            publishedAt = publishedAt,
        )
}
