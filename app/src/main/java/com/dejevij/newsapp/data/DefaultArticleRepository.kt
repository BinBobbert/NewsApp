package com.dejevij.newsapp.data

import com.dejevij.newsapp.data.network.NewsApi
import com.dejevij.newsapp.data.network.model.toArticleOrNull
import com.dejevij.newsapp.model.Article
import com.dejevij.newsapp.model.Category
import com.dejevij.newsapp.model.Country
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import okhttp3.CacheControl
import retrofit2.HttpException

@Singleton
class DefaultArticleRepository @Inject constructor(private val newsApi: NewsApi) :
    ArticleRepository {

    private val cache = MutableStateFlow<List<Article>>(emptyList())

    override val topHeadlines: Flow<List<Article>> = cache.asStateFlow()

    override fun observeArticle(url: String): Flow<Article?> = cache
        .map { articles -> articles.firstOrNull { it.url == url } }
        .distinctUntilChanged()

    // Only specific exceptions are caught: a catch-all would also swallow
    // CancellationException and break structured concurrency.
    override suspend fun refreshTopHeadlines(
        country: Country,
        categories: Set<Category>,
        forceRefresh: Boolean,
    ): RefreshResult = try {
        val cacheControl = if (forceRefresh) CacheControl.FORCE_NETWORK.toString() else null
        cache.value = fetchTopHeadlines(country, categories, cacheControl)
            .mapNotNull { it.toArticleOrNull() }
            .distinctBy { it.url }
            .sortedByDescending { it.publishedAt }
        RefreshResult.Success
    } catch (e: HttpException) {
        RefreshResult.Failure(e.toDataError())
    } catch (_: IOException) {
        RefreshResult.Failure(DataError.NoConnection)
    } catch (_: SerializationException) {
        RefreshResult.Failure(DataError.Unknown)
    }

    // NewsAPI accepts a single category per request, so multiple categories are fetched in
    // parallel. If any request fails, the whole refresh fails and the cache keeps its articles.
    private suspend fun fetchTopHeadlines(
        country: Country,
        categories: Set<Category>,
        cacheControl: String?,
    ) = if (categories.isEmpty()) {
        fetchTopHeadlines(country, category = null, cacheControl)
    } else {
        coroutineScope {
            categories.map { category ->
                async { fetchTopHeadlines(country, category.apiValue, cacheControl) }
            }.awaitAll().flatten()
        }
    }

    private suspend fun fetchTopHeadlines(
        country: Country,
        category: String?,
        cacheControl: String?,
    ) = newsApi.getTopHeadlines(
        country = country.code,
        category = category,
        pageSize = PAGE_SIZE,
        cacheControl = cacheControl,
    ).articles

    private fun HttpException.toDataError(): DataError = when (code()) {
        HTTP_UNAUTHORIZED -> DataError.Unauthorized
        HTTP_TOO_MANY_REQUESTS -> DataError.RateLimited
        else -> DataError.Server(code())
    }

    private companion object {

        const val PAGE_SIZE = 50
        const val HTTP_UNAUTHORIZED = 401
        const val HTTP_TOO_MANY_REQUESTS = 429
    }
}
