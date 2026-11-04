package com.dejevij.newsapp.ui.articlelist

import com.dejevij.newsapp.model.Category

data class ArticleListUiState(
    val content: ArticleListContent,
    val country: CountryViewData,
    val countries: List<CountryViewData> = emptyList(),
    val selectedCategories: Set<Category> = emptySet(),
    val isRefreshing: Boolean = false,
)
