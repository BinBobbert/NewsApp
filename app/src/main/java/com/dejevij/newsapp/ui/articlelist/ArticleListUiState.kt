package com.dejevij.newsapp.ui.articlelist

import com.dejevij.newsapp.data.DataError
import com.dejevij.newsapp.model.Category
import com.dejevij.newsapp.model.Country

data class ArticleListUiState(
    val content: ArticleListContent,
    val country: Country,
    val selectedCategories: Set<Category> = emptySet(),
    val isRefreshing: Boolean = false,
    val refreshError: DataError? = null
)

sealed interface ArticleListContent {

    data object Loading : ArticleListContent

    data object Empty : ArticleListContent

    data class Error(val error: DataError) : ArticleListContent

    data class Articles(val articles: List<ArticleViewData>) : ArticleListContent
}
