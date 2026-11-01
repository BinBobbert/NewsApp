package com.dejevij.newsapp.data

import com.dejevij.newsapp.data.network.NewsApi
import com.dejevij.newsapp.data.network.model.ArticleResponse
import com.dejevij.newsapp.data.network.model.TopHeadlinesResponse
import com.dejevij.newsapp.data.network.model.articleResponse
import com.dejevij.newsapp.model.Country
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

        val result = repository.refreshTopHeadlines(Country.Default)

        assertEquals(RefreshResult.Success, result)
        assertEquals(listOf("a", "b"), repository.topHeadlines.first().map { it.url })
    }

    @Test
    fun `refresh requests headlines for the given country`() = runTest {
        newsApi.response = { headlines() }

        repository.refreshTopHeadlines(requireNotNull(Country.fromCode("gb")))

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

        repository.refreshTopHeadlines(Country.Default)

        val articles = repository.topHeadlines.first()
        assertEquals(listOf("a", "b"), articles.map { it.url })
        assertEquals("First", articles.first().title)
    }

    @Test
    fun `refresh skips removed articles`() = runTest {
        newsApi.response = {
            headlines(articleResponse(url = "a"), articleResponse(url = "b", title = "[Removed]"))
        }

        repository.refreshTopHeadlines(Country.Default)

        assertEquals(listOf("a"), repository.topHeadlines.first().map { it.url })
    }

    @Test
    fun `failed refresh keeps previously loaded articles`() = runTest {
        newsApi.response = { headlines(articleResponse(url = "a")) }
        repository.refreshTopHeadlines(Country.Default)
        newsApi.response = { throw IOException() }

        val result = repository.refreshTopHeadlines(Country.Default)

        assertEquals(RefreshResult.Failure(DataError.NoConnection), result)
        assertEquals(listOf("a"), repository.topHeadlines.first().map { it.url })
    }

    @Test
    fun `maps a malformed response to an unknown error`() = runTest {
        newsApi.response = { throw SerializationException("Malformed JSON") }

        val result = repository.refreshTopHeadlines(Country.Default)

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
                repository.refreshTopHeadlines(Country.Default),
            )
        }
    }

    @Test
    fun `observeArticle emits the article with the matching url`() = runTest {
        newsApi.response = { headlines(articleResponse(url = "a"), articleResponse(url = "b")) }
        repository.refreshTopHeadlines(Country.Default)

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
        var response: () -> TopHeadlinesResponse = { error("No response set") }
        var requestedCountry: String? = null

        override suspend fun getTopHeadlines(country: String, pageSize: Int): TopHeadlinesResponse {
            requestedCountry = country
            return response()
        }
    }
}
