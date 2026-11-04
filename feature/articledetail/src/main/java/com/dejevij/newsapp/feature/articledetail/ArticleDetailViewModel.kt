package com.dejevij.newsapp.feature.articledetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dejevij.newsapp.core.data.ArticleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = ArticleDetailViewModel.Factory::class)
class ArticleDetailViewModel @AssistedInject constructor(
    @Assisted url: String,
    articleRepository: ArticleRepository,
    mapper: ArticleDetailViewDataMapper,
) : ViewModel() {

    val uiState: StateFlow<ArticleDetailUiState> = articleRepository.observeArticle(url)
        .map { article ->
            if (article == null) {
                ArticleDetailUiState.NotAvailable
            } else {
                ArticleDetailUiState.Content(mapper.map(article))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = ArticleDetailUiState.Loading,
        )

    @AssistedFactory
    interface Factory {
        fun create(url: String): ArticleDetailViewModel
    }

    private companion object {

        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
