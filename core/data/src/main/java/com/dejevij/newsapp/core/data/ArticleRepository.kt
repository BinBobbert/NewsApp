package com.dejevij.newsapp.core.data

import com.dejevij.newsapp.core.model.Article
import com.dejevij.newsapp.core.model.Category
import com.dejevij.newsapp.core.model.Country
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {

    fun topHeadlines(country: Country, categories: Set<Category>): Flow<List<Article>>

    fun observeArticle(url: String): Flow<Article?>

    suspend fun refreshTopHeadlines(
        country: Country,
        categories: Set<Category>,
        forceRefresh: Boolean,
    ): RefreshResult
}
