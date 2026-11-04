package com.dejevij.newsapp.feature.articlelist

import com.dejevij.newsapp.core.data.DataError
import com.dejevij.newsapp.core.model.Category

data class ArticleListUiState(
    val content: ArticleListContent,
    val country: CountryViewData,
    val countries: List<CountryViewData> = emptyList(),
    val selectedCategories: Set<Category> = emptySet(),
    val isRefreshing: Boolean = false,
    val refreshError: DataError? = null,
)
