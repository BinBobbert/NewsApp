package com.dejevij.newsapp.feature.articledetail

sealed interface ArticleDetailUiState {

    data object Loading : ArticleDetailUiState

    data object NotAvailable : ArticleDetailUiState

    data class Content(val article: ArticleDetailViewData) : ArticleDetailUiState
}
