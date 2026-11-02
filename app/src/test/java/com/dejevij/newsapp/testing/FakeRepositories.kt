package com.dejevij.newsapp.testing

import com.dejevij.newsapp.data.ArticleRepository
import com.dejevij.newsapp.data.CountryRepository
import com.dejevij.newsapp.data.RefreshResult
import com.dejevij.newsapp.model.Article
import com.dejevij.newsapp.model.Category
import com.dejevij.newsapp.model.Country
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeArticleRepository : ArticleRepository {

    private val articles = MutableStateFlow<List<Article>>(emptyList())
    val requests = mutableListOf<Pair<Country, Set<Category>>>()
    val forceRefreshes = mutableListOf<Boolean>()
    var nextResult: RefreshResult = RefreshResult.Success
    var nextArticles: List<Article> = emptyList()

    override val topHeadlines: Flow<List<Article>> = articles

    override fun observeArticle(url: String): Flow<Article?> =
        articles.map { list -> list.firstOrNull { it.url == url } }

    override suspend fun refreshTopHeadlines(
        country: Country,
        categories: Set<Category>,
        forceRefresh: Boolean,
    ): RefreshResult {
        requests += country to categories
        forceRefreshes += forceRefresh
        val result = nextResult
        if (result == RefreshResult.Success) articles.value = nextArticles
        return result
    }
}

class FakeCountryRepository(initial: Country = Country.Default) : CountryRepository {

    private val state = MutableStateFlow(initial)

    override val country: Flow<Country> = state

    override suspend fun setCountry(country: Country) {
        state.value = country
    }
}
