package com.dejevij.newsapp.ui.articlelist

import com.dejevij.newsapp.data.DataError

sealed interface ArticleListContent {

    data object Loading : ArticleListContent

    data object Empty : ArticleListContent

    data class Error(val error: DataError) : ArticleListContent

    data class Articles(val articles: List<ArticleViewData>) : ArticleListContent
}
