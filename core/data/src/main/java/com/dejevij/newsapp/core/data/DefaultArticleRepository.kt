package com.dejevij.newsapp.core.data

import com.dejevij.newsapp.core.database.dao.HeadlineDao
import com.dejevij.newsapp.core.database.model.ArticleEntity
import com.dejevij.newsapp.core.database.model.toArticle
import com.dejevij.newsapp.core.database.model.toEntity
import com.dejevij.newsapp.core.model.Article
import com.dejevij.newsapp.core.model.Category
import com.dejevij.newsapp.core.model.Country
import com.dejevij.newsapp.core.network.NewsApi
import com.dejevij.newsapp.core.network.model.toArticleOrNull
import java.io.IOException
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

// The database is the single source of truth: screens observe it, and a refresh only writes to it.
class DefaultArticleRepository @Inject constructor(
    private val newsApi: NewsApi,
    private val headlineDao: HeadlineDao,
    private val clock: Clock,
) : ArticleRepository {

    override fun topHeadlines(country: Country, categories: Set<Category>): Flow<List<Article>> =
        headlineDao.observeHeadlines(country.code, categories.toKeys())
            .map { articles -> articles.map(ArticleEntity::toArticle) }
            .distinctUntilChanged()

    override fun observeArticle(url: String): Flow<Article?> = headlineDao.observeArticle(url)
        .map { it?.toArticle() }
        .distinctUntilChanged()

    // Only specific exceptions are caught: a catch-all would also swallow
    // CancellationException and break structured concurrency.
    override suspend fun refreshTopHeadlines(
        country: Country,
        categories: Set<Category>,
        forceRefresh: Boolean,
    ): RefreshResult = try {
        val now = clock.now()
        val keys = categories.toKeys()
        val staleKeys = if (forceRefresh) keys else staleKeys(country, keys, now)
        if (staleKeys.isNotEmpty()) {
            headlineDao.replaceHeadlines(
                country = country.code,
                headlines = fetchTopHeadlines(country, staleKeys),
                fetchedAt = now.toEpochMilliseconds(),
            )
        }
        RefreshResult.Success
    } catch (e: HttpException) {
        RefreshResult.Failure(e.toDataError())
    } catch (_: IOException) {
        RefreshResult.Failure(DataError.NoConnection)
    } catch (_: SerializationException) {
        RefreshResult.Failure(DataError.Unknown)
    }

    private suspend fun staleKeys(
        country: Country,
        keys: List<String>,
        now: Instant,
    ): List<String> {
        val fetchedAt = headlineDao.getQueries(country.code, keys)
            .associate { it.category to Instant.fromEpochMilliseconds(it.fetchedAt) }
        return keys.filter { key -> fetchedAt[key]?.let { now - it >= MAX_AGE } ?: true }
    }

    // NewsAPI accepts a single category per request, so categories are fetched in parallel.
    // If any request fails, nothing is written and the database keeps its articles.
    private suspend fun fetchTopHeadlines(
        country: Country,
        keys: List<String>,
    ): Map<String, List<ArticleEntity>> = coroutineScope {
        keys.map { key ->
            async {
                key to newsApi.getTopHeadlines(
                    country = country.code,
                    category = key.takeUnless { it == ALL_CATEGORIES },
                    pageSize = PAGE_SIZE,
                ).articles
                    .mapNotNull { it.toArticleOrNull()?.toEntity() }
                    .distinctBy { it.url }
            }
        }.awaitAll().toMap()
    }

    private fun Set<Category>.toKeys(): List<String> =
        map { it.apiValue }.ifEmpty { listOf(ALL_CATEGORIES) }

    private fun HttpException.toDataError(): DataError = when (code()) {
        HTTP_UNAUTHORIZED -> DataError.Unauthorized
        HTTP_TOO_MANY_REQUESTS -> DataError.RateLimited
        else -> DataError.Server(code())
    }

    private companion object {

        const val ALL_CATEGORIES = "all"
        const val PAGE_SIZE = 50
        const val HTTP_UNAUTHORIZED = 401
        const val HTTP_TOO_MANY_REQUESTS = 429
        val MAX_AGE = 30.minutes
    }
}
