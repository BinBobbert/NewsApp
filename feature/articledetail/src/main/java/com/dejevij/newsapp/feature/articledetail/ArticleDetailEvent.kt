package com.dejevij.newsapp.feature.articledetail

sealed interface ArticleDetailEvent {

    data class OpenArticle(val url: String) : ArticleDetailEvent

    data object NavigateBack : ArticleDetailEvent
}
