package com.dejevij.newsapp.feature.articledetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dejevij.newsapp.core.data.ArticleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ArticleDetailViewModel.Factory::class)
class ArticleDetailViewModel @AssistedInject constructor(
    @Assisted private val url: String,
    articleRepository: ArticleRepository,
    mapper: ArticleDetailViewDataMapper,
) : ViewModel() {

    private val eventChannel = Channel<ArticleDetailEvent>(Channel.BUFFERED)

    val events: Flow<ArticleDetailEvent> = eventChannel.receiveAsFlow()

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

    fun onReadFullArticleClick() {
        viewModelScope.launch { eventChannel.send(ArticleDetailEvent.OpenArticle(url)) }
    }

    fun onBackClick() {
        viewModelScope.launch { eventChannel.send(ArticleDetailEvent.NavigateBack) }
    }

    @AssistedFactory
    interface Factory {
        fun create(url: String): ArticleDetailViewModel
    }

    private companion object {

        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
