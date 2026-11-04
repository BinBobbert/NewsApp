package com.dejevij.newsapp.feature.articlelist

import com.dejevij.newsapp.core.data.DataError

sealed interface ArticleListEvent {

    data class NavigateToArticle(val url: String) : ArticleListEvent

    data class ShowRefreshError(val error: DataError) : ArticleListEvent
}
