package com.dejevij.newsapp.data

import com.dejevij.newsapp.model.Article
import com.dejevij.newsapp.model.Country
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {

    val topHeadlines: Flow<List<Article>>

    fun observeArticle(url: String): Flow<Article?>

    suspend fun refreshTopHeadlines(country: Country): RefreshResult
}
