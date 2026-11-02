package com.dejevij.newsapp.ui.articledetail

sealed interface ArticleDetailEvent {

    data class OpenArticle(val url: String) : ArticleDetailEvent

    data object NavigateBack : ArticleDetailEvent
}
