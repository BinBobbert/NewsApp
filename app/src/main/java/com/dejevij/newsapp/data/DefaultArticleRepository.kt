package com.dejevij.newsapp.data

import com.dejevij.newsapp.data.network.NewsApi
import com.dejevij.newsapp.data.network.model.toArticleOrNull
import com.dejevij.newsapp.model.Article
import com.dejevij.newsapp.model.Country
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
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
    override suspend fun refreshTopHeadlines(country: Country): RefreshResult = try {
        val response = newsApi.getTopHeadlines(country = country.code, pageSize = PAGE_SIZE)
        cache.value = response.articles.mapNotNull { it.toArticleOrNull() }
        RefreshResult.Success
    } catch (e: HttpException) {
        RefreshResult.Failure(e.toDataError())
    } catch (_: IOException) {
        RefreshResult.Failure(DataError.NoConnection)
    } catch (_: SerializationException) {
        RefreshResult.Failure(DataError.Unknown)
    }

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
