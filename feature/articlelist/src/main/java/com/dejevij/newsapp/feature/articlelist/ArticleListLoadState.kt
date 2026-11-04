package com.dejevij.newsapp.feature.articlelist

import com.dejevij.newsapp.core.data.DataError

internal sealed interface ArticleListLoadState {

    data object Loading : ArticleListLoadState

    data object Refreshing : ArticleListLoadState

    data object Idle : ArticleListLoadState

    data class Failed(val error: DataError) : ArticleListLoadState
}
