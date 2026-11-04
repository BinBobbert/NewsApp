package com.dejevij.newsapp.core.data

import com.dejevij.newsapp.core.model.Category
import com.dejevij.newsapp.core.model.Country
import com.dejevij.newsapp.core.network.NewsApi
import com.dejevij.newsapp.core.network.model.ArticleResponse
import com.dejevij.newsapp.core.network.model.TopHeadlinesResponse
import com.dejevij.newsapp.core.testing.articleResponse
import java.io.IOException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class DefaultArticleRepositoryTest {

    private val newsApi = FakeNewsApi()
    private val repository = DefaultArticleRepository(newsApi)

    @Test
    fun `refresh stores mapped articles`() = runTest {
        newsApi.response = { headlines(articleResponse(url = "a"), articleResponse(url = "b")) }

        val result = repository.refreshTopHeadlines(
            Country.Default,
            emptySet(),
            forceRefresh = false,
        )

        assertEquals(RefreshResult.Success, result)
        assertEquals(listOf("a", "b"), repository.topHeadlines.first().map { it.url })
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

        val articles = repository.topHeadlines.first()
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
        assertEquals(listOf("new", "shared", "old"), repository.topHeadlines.first().map { it.url })
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
        assertEquals(listOf("a"), repository.topHeadlines.first().map { it.url })
    }

    @Test
    fun `only a forced refresh bypasses the http cache`() = runTest {
        newsApi.response = { headlines() }

        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)
        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = true)

        assertEquals(listOf(null, "no-cache"), newsApi.requestedCacheControls)
    }

    @Test
    fun `refresh skips removed articles`() = runTest {
        newsApi.response = {
            headlines(articleResponse(url = "a"), articleResponse(url = "b", title = "[Removed]"))
        }

        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)

        assertEquals(listOf("a"), repository.topHeadlines.first().map { it.url })
    }

    @Test
    fun `failed refresh keeps previously loaded articles`() = runTest {
        newsApi.response = { headlines(articleResponse(url = "a")) }
        repository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)
        newsApi.response = { throw IOException() }

        val result = repository.refreshTopHeadlines(
            Country.Default,
            emptySet(),
            forceRefresh = false,
        )

        assertEquals(RefreshResult.Failure(DataError.NoConnection), result)
        assertEquals(listOf("a"), repository.topHeadlines.first().map { it.url })
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
        val requestedCacheControls = mutableListOf<String?>()

        override suspend fun getTopHeadlines(
            country: String,
            category: String?,
            pageSize: Int,
            cacheControl: String?,
        ): TopHeadlinesResponse {
            requestedCountry = country
            requestedCategories += category
            requestedCacheControls += cacheControl
            return response(category)
        }
    }
}
