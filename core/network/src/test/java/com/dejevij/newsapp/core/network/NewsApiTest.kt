package com.dejevij.newsapp.core.network

import com.dejevij.newsapp.core.network.di.NetworkModule
import com.dejevij.newsapp.core.network.model.ArticleResponse
import com.dejevij.newsapp.core.network.model.SourceResponse
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class NewsApiTest {

    private val server = MockWebServer()
    private lateinit var api: NewsApi

    @Before
    fun setUp() {
        server.start()
        val okHttpClient = NetworkModule.provideNewsApiOkHttpClient(
            okHttpClient = NetworkModule.provideOkHttpClient(),
            apiKeyInterceptor = ApiKeyInterceptor(API_KEY),
            debugInterceptors = emptySet(),
        )
        val retrofit = NetworkModule.provideRetrofit(
            okHttpClient = okHttpClient,
            json = NetworkModule.provideJson(),
            baseUrl = server.url("/").toString(),
        )
        api = NetworkModule.provideNewsApi(retrofit)
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `getTopHeadlines sends api key header and query parameters`() = runTest {
        server.enqueue(MockResponse.Builder().body(readResource("top_headlines.json")).build())

        api.getTopHeadlines(country = "us", category = null, pageSize = 20)

        val request = server.takeRequest()
        assertEquals(API_KEY, request.headers[ApiKeyInterceptor.HEADER])
        assertEquals("/v2/top-headlines", request.url.encodedPath)
        assertEquals("us", request.url.queryParameter("country"))
        assertEquals("20", request.url.queryParameter("pageSize"))
    }

    @Test
    fun `getTopHeadlines parses articles including nulls and unknown fields`() = runTest {
        server.enqueue(MockResponse.Builder().body(readResource("top_headlines.json")).build())

        val response = api.getTopHeadlines(
            country = "us",
            category = null,
            pageSize = 20,
        )

        assertEquals("ok", response.status)
        assertEquals(2, response.totalResults)
        assertEquals(
            ArticleResponse(
                source = SourceResponse(id = null, name = "Example"),
                author = null,
                title = "Second headline",
                description = null,
                url = "https://example.com/second",
                urlToImage = null,
                publishedAt = "2026-11-01T08:00:00Z",
                content = null,
            ),
            response.articles[1],
        )
    }

    @Test
    fun `getTopHeadlines throws HttpException on error response`() = runTest {
        server.enqueue(
            MockResponse.Builder()
                .code(401)
                .body("""{"status":"error","code":"apiKeyInvalid","message":"Invalid key"}""")
                .build(),
        )

        try {
            api.getTopHeadlines(country = "us", category = null, pageSize = 20)
            fail("Expected HttpException")
        } catch (e: HttpException) {
            assertEquals(401, e.code())
        }
    }

    private fun readResource(name: String): String =
        requireNotNull(javaClass.classLoader?.getResource(name)).readText()

    private companion object {

        const val API_KEY = "test-api-key"
    }
}
