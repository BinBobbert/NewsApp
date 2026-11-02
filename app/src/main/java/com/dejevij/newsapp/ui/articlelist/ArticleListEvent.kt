package com.dejevij.newsapp.ui.articlelist

import com.dejevij.newsapp.data.DataError

sealed interface ArticleListEvent {

    data class OpenArticle(val url: String) : ArticleListEvent

    data class ShowRefreshError(val error: DataError) : ArticleListEvent
}
