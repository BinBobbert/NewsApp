package com.dejevij.newsapp.data.network

import com.dejevij.newsapp.data.network.model.ArticleResponse
import com.dejevij.newsapp.data.network.model.SourceResponse
import com.dejevij.newsapp.di.NetworkModule
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.Cache
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import retrofit2.HttpException

class NewsApiTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val server = MockWebServer()
    private lateinit var api: NewsApi

    @Before
    fun setUp() {
        server.start()
        val okHttpClient = NetworkModule.provideNewsApiOkHttpClient(
            okHttpClient = NetworkModule.provideOkHttpClient(),
            apiKeyInterceptor = ApiKeyInterceptor(API_KEY),
            responseCacheInterceptor = ResponseCacheInterceptor(),
            cache = Cache(temporaryFolder.newFolder("http-cache"), CACHE_MAX_BYTES),
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

        api.getTopHeadlines(country = "us", category = null, pageSize = 20, cacheControl = null)

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
            cacheControl = null,
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
            api.getTopHeadlines(country = "us", category = null, pageSize = 20, cacheControl = null)
            fail("Expected HttpException")
        } catch (e: HttpException) {
            assertEquals(401, e.code())
        }
    }

    @Test
    fun `a repeated request is answered from the cache despite no-cache from the server`() =
        runTest {
            server.enqueue(headlinesResponse())

            topHeadlines()
            topHeadlines()

            assertEquals(1, server.requestCount)
        }

    @Test
    fun `a no-cache request bypasses the cache`() = runTest {
        server.enqueue(headlinesResponse())
        server.enqueue(headlinesResponse())

        topHeadlines()
        topHeadlines(cacheControl = "no-cache")

        assertEquals(2, server.requestCount)
    }

    @Test
    fun `different categories are cached separately`() = runTest {
        server.enqueue(headlinesResponse())
        server.enqueue(headlinesResponse())

        topHeadlines(category = "sports")
        topHeadlines(category = "technology")
        topHeadlines(category = "sports")

        assertEquals(2, server.requestCount)
    }

    @Test
    fun `error responses are not cached`() = runTest {
        server.enqueue(MockResponse.Builder().code(500).build())
        server.enqueue(headlinesResponse())

        runCatching { topHeadlines() }
        topHeadlines()

        assertEquals(2, server.requestCount)
    }

    private suspend fun topHeadlines(category: String? = null, cacheControl: String? = null) =
        api.getTopHeadlines(
            country = "us",
            category = category,
            pageSize = 20,
            cacheControl = cacheControl,
        )

    private fun headlinesResponse() = MockResponse.Builder()
        .body(readResource("top_headlines.json"))
        .addHeader("Cache-Control", "no-cache")
        .build()

    private fun readResource(name: String): String =
        requireNotNull(javaClass.classLoader?.getResource(name)).readText()

    private companion object {
        const val API_KEY = "test-api-key"
        const val CACHE_MAX_BYTES = 1024L * 1024
    }
}
