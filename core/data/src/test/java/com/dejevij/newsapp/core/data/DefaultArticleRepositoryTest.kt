package com.dejevij.newsapp.core.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dejevij.newsapp.core.database.NewsDatabase
import com.dejevij.newsapp.core.model.Category
import com.dejevij.newsapp.core.model.Country
import com.dejevij.newsapp.core.network.NewsApi
import com.dejevij.newsapp.core.network.model.ArticleResponse
import com.dejevij.newsapp.core.network.model.TopHeadlinesResponse
import com.dejevij.newsapp.core.testing.articleResponse
import java.io.IOException
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.HttpException
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class DefaultArticleRepositoryTest {

    private val database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext<Context>(),
        NewsDatabase::class.java,
    ).allowMainThreadQueries().build()
    private val newsApi = FakeNewsApi()
    private var now = Instant.parse("2026-11-01T12:00:00Z")
    private val clock = object : Clock {
        override fun now() = now
    }
    private val repository = DefaultArticleRepository(newsApi, database.headlineDao(), clock)

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `refresh stores mapped articles`() = runTest {
        newsApi.response = { headlines(articleResponse(url = "a"), articleResponse(url = "b")) }

        val result = repository.refreshTopHeadlines(
            Country.Default,
            emptySet(),
            forceRefresh = false,
        )

        assertEquals(RefreshResult.Success, result)
        assertEquals(listOf("a", "b"), storedHeadlines().map { it.url })
    }

    @Test
    fun `refresh requests headlines for the given country`() = runTest {
        newsApi.response = { headlines() }

        repository.refreshTopHeadlines(
            requireNotNull(Country.fromCode("gb")),
            emptySet(),
            forceRefresh = false,
        )

        assertEquals("gb", newsApi.requestedCountry)
    }

    @Test
    fun `refresh keeps only the first article for each url`() = runTest {
        newsApi.response = {
            headlines(
                articleResponse(url = "a", title = "First"),
                articleResponse(url = "b"),
                articleResponse(url = "a", title = "Duplicate"),
            )
        }

        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)

        val articles = storedHeadlines()
        assertEquals(listOf("a", "b"), articles.map { it.url })
        assertEquals("First", articles.first().title)
    }

    @Test
    fun `refresh without categories makes a single request without a category`() = runTest {
        newsApi.response = { headlines(articleResponse(url = "a")) }

        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)

        assertEquals(listOf(null), newsApi.requestedCategories)
    }

    @Test
    fun `refresh with categories merges one request per category newest first`() = runTest {
        newsApi.response = { category ->
            when (category) {
                "sports" -> headlines(
                    articleResponse(url = "old", publishedAt = "2026-11-01T08:00:00Z"),
                    articleResponse(url = "shared", publishedAt = "2026-11-01T10:00:00Z"),
                )

                else -> headlines(
                    articleResponse(url = "new", publishedAt = "2026-11-01T12:00:00Z"),
                    articleResponse(url = "shared", publishedAt = "2026-11-01T10:00:00Z"),
                )
            }
        }

        val result = repository.refreshTopHeadlines(
            Country.Default,
            setOf(Category.SPORTS, Category.TECHNOLOGY),
            forceRefresh = false,
        )

        assertEquals(RefreshResult.Success, result)
        assertEquals(setOf("sports", "technology"), newsApi.requestedCategories.toSet())
        assertEquals(
            listOf("new", "shared", "old"),
            storedHeadlines(Category.SPORTS, Category.TECHNOLOGY).map { it.url },
        )
    }

    @Test
    fun `refresh fails and keeps the cache when one category request fails`() = runTest {
        newsApi.response = { headlines(articleResponse(url = "a")) }
        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)
        newsApi.response = { category ->
            if (category == "sports") throw IOException() else headlines(articleResponse(url = "b"))
        }

        val result = repository.refreshTopHeadlines(
            Country.Default,
            setOf(Category.SPORTS, Category.TECHNOLOGY),
            forceRefresh = false,
        )

        assertEquals(RefreshResult.Failure(DataError.NoConnection), result)
        assertEquals(listOf("a"), storedHeadlines().map { it.url })
    }

    @Test
    fun `refresh skips the network while the stored headlines are fresh`() = runTest {
        newsApi.response = { headlines(articleResponse(url = "a")) }
        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)
        now += 29.minutes

        val result = repository.refreshTopHeadlines(
            Country.Default,
            emptySet(),
            forceRefresh = false,
        )

        assertEquals(RefreshResult.Success, result)
        assertEquals(1, newsApi.requestedCategories.size)
    }

    @Test
    fun `refresh fetches again once the stored headlines are stale`() = runTest {
        newsApi.response = { headlines(articleResponse(url = "a")) }
        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)
        now += 30.minutes

        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)

        assertEquals(2, newsApi.requestedCategories.size)
    }

    @Test
    fun `a forced refresh fetches even when the stored headlines are fresh`() = runTest {
        newsApi.response = { headlines(articleResponse(url = "a")) }
        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)

        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = true)

        assertEquals(2, newsApi.requestedCategories.size)
    }

    @Test
    fun `refresh only fetches the categories that are stale`() = runTest {
        newsApi.response = { headlines(articleResponse(url = "a")) }
        repository.refreshTopHeadlines(
            Country.Default,
            setOf(Category.SPORTS),
            forceRefresh = false,
        )
        newsApi.requestedCategories.clear()

        repository.refreshTopHeadlines(
            Country.Default,
            setOf(Category.SPORTS, Category.HEALTH),
            forceRefresh = false,
        )

        assertEquals(listOf("health"), newsApi.requestedCategories)
    }

    @Test
    fun `headlines are stored per country`() = runTest {
        val gb = requireNotNull(Country.fromCode("gb"))
        newsApi.response = { headlines(articleResponse(url = "us")) }
        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)
        newsApi.response = { headlines(articleResponse(url = "gb")) }
        repository.refreshTopHeadlines(gb, emptySet(), forceRefresh = false)

        assertEquals(listOf("us"), storedHeadlines().map { it.url })
        assertEquals(
            listOf("gb"),
            repository.topHeadlines(gb, emptySet()).first().map { it.url },
        )
    }

    @Test
    fun `refresh skips removed articles`() = runTest {
        newsApi.response = {
            headlines(articleResponse(url = "a"), articleResponse(url = "b", title = "[Removed]"))
        }

        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)

        assertEquals(listOf("a"), storedHeadlines().map { it.url })
    }

    @Test
    fun `failed refresh keeps previously loaded articles`() = runTest {
        newsApi.response = { headlines(articleResponse(url = "a")) }
        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)
        newsApi.response = { throw IOException() }

        val result = repository.refreshTopHeadlines(
            Country.Default,
            emptySet(),
            forceRefresh = true,
        )

        assertEquals(RefreshResult.Failure(DataError.NoConnection), result)
        assertEquals(listOf("a"), storedHeadlines().map { it.url })
    }

    @Test
    fun `maps a malformed response to an unknown error`() = runTest {
        newsApi.response = { throw SerializationException("Malformed JSON") }

        val result = repository.refreshTopHeadlines(
            Country.Default,
            emptySet(),
            forceRefresh = false,
        )

        assertEquals(RefreshResult.Failure(DataError.Unknown), result)
    }

    @Test
    fun `maps http errors to data errors`() = runTest {
        val expected = mapOf(
            401 to DataError.Unauthorized,
            429 to DataError.RateLimited,
            500 to DataError.Server(500),
        )

        expected.forEach { (code, error) ->
            newsApi.response = { throw httpException(code) }
            assertEquals(
                RefreshResult.Failure(error),
                repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false),
            )
        }
    }

    @Test
    fun `observeArticle emits the article with the matching url`() = runTest {
        newsApi.response = { headlines(articleResponse(url = "a"), articleResponse(url = "b")) }
        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)

        assertEquals("b", repository.observeArticle("b").first()?.url)
        assertNull(repository.observeArticle("missing").first())
    }

    private suspend fun storedHeadlines(vararg categories: Category) =
        repository.topHeadlines(Country.Default, categories.toSet()).first()

    private fun headlines(vararg articles: ArticleResponse) = TopHeadlinesResponse(
        status = "ok",
        totalResults = articles.size,
        articles = articles.toList(),
    )

    private fun httpException(code: Int) =
        HttpException(Response.error<Any>(code, "".toResponseBody()))

    private class FakeNewsApi : NewsApi {
        var response: (category: String?) -> TopHeadlinesResponse = { error("No response set") }
        var requestedCountry: String? = null
        val requestedCategories = mutableListOf<String?>()

        override suspend fun getTopHeadlines(
            country: String,
            category: String?,
            pageSize: Int,
        ): TopHeadlinesResponse {
            requestedCountry = country
            requestedCategories += category
            return response(category)
        }
    }
}
