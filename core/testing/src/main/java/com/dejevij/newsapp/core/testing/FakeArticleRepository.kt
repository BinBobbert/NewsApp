package com.dejevij.newsapp.core.testing

import com.dejevij.newsapp.core.data.ArticleRepository
import com.dejevij.newsapp.core.data.RefreshResult
import com.dejevij.newsapp.core.model.Article
import com.dejevij.newsapp.core.model.Category
import com.dejevij.newsapp.core.model.Country
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeArticleRepository : ArticleRepository {

    private val articles = MutableStateFlow<List<Article>>(emptyList())
    val requests = mutableListOf<Pair<Country, Set<Category>>>()
    val forceRefreshes = mutableListOf<Boolean>()
    var nextResult: RefreshResult = RefreshResult.Success
    var nextArticles: List<Article> = emptyList()
    var refreshGate: CompletableDeferred<Unit>? = null

    override fun topHeadlines(country: Country, categories: Set<Category>): Flow<List<Article>> =
        articles

    fun setCachedArticles(articles: List<Article>) {
        this.articles.value = articles
    }

    override fun observeArticle(url: String): Flow<Article?> =
        articles.map { list -> list.firstOrNull { it.url == url } }

    override suspend fun refreshTopHeadlines(
        country: Country,
        categories: Set<Category>,
        forceRefresh: Boolean,
    ): RefreshResult {
        requests += country to categories
        forceRefreshes += forceRefresh
        refreshGate?.await()
        val result = nextResult
        if (result == RefreshResult.Success) articles.value = nextArticles
        return result
    }
}
