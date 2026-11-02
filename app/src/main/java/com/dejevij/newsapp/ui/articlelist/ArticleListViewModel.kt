package com.dejevij.newsapp.ui.articlelist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dejevij.newsapp.data.ArticleRepository
import com.dejevij.newsapp.data.CountryRepository
import com.dejevij.newsapp.data.RefreshResult
import com.dejevij.newsapp.model.Category
import com.dejevij.newsapp.model.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val countryRepository: CountryRepository,
    private val articleMapper: ArticleViewDataMapper,
    countryMapper: CountryViewDataMapper,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val countries = countryMapper.map(Country.all)
    private val refreshRequests = MutableSharedFlow<ArticleListLoadState>(extraBufferCapacity = 1)
    private val eventChannel = Channel<ArticleListEvent>(Channel.BUFFERED)

    private val selectedCategories: Flow<Set<Category>> = savedStateHandle
        .getStateFlow(CATEGORIES_KEY, emptyList<String>())
        .map { names -> names.map(Category::valueOf).toSet() }

    // Starts a full load whenever collection starts or the filters change, and a refresh for
    // every refresh request. The *Latest operators cancel a load that a newer one replaces.
    @OptIn(ExperimentalCoroutinesApi::class)
    private val loadState: Flow<ArticleListLoadState> =
        combine(countryRepository.country, selectedCategories, ::Pair)
            .distinctUntilChanged()
            .flatMapLatest { (country, categories) ->
                refreshRequests
                    .onStart { emit(ArticleListLoadState.Loading) }
                    .transformLatest { mode ->
                        emit(mode)
                        emit(loadArticles(country, categories, mode))
                    }
            }

    val events: Flow<ArticleListEvent> = eventChannel.receiveAsFlow()

    val uiState: StateFlow<ArticleListUiState> = combine(
        articleRepository.topHeadlines,
        countryRepository.country,
        selectedCategories,
        loadState,
    ) { articles, country, categories, load ->
        ArticleListUiState(
            content = when (load) {
                ArticleListLoadState.Loading -> ArticleListContent.Loading

                is ArticleListLoadState.Failed -> ArticleListContent.Error(load.error)

                ArticleListLoadState.Idle, ArticleListLoadState.Refreshing ->
                    if (articles.isEmpty()) {
                        ArticleListContent.Empty
                    } else {
                        ArticleListContent.Articles(articles.map(articleMapper::map))
                    }
            },
            country = countries.first { it.country == country },
            countries = countries,
            selectedCategories = categories,
            isRefreshing = load == ArticleListLoadState.Refreshing,
        )
    }.stateIn(
        scope = viewModelScope,
        // Lazily rather than WhileSubscribed: restarting this pipeline starts a new full load,
        // which would reset the list and its scroll position whenever the screen returns.
        started = SharingStarted.Lazily,
        initialValue = ArticleListUiState(
            content = ArticleListContent.Loading,
            country = countries.first { it.country == Country.forLocale(Locale.getDefault()) },
            countries = countries,
        ),
    )

    fun onArticleClick(url: String) {
        viewModelScope.launch { eventChannel.send(ArticleListEvent.NavigateToArticle(url)) }
    }

    fun onRefresh() {
        refreshRequests.tryEmit(ArticleListLoadState.Refreshing)
    }

    fun onRetry() {
        val isShowingError = uiState.value.content is ArticleListContent.Error
        refreshRequests.tryEmit(
            if (isShowingError) ArticleListLoadState.Loading else ArticleListLoadState.Refreshing,
        )
    }

    fun onCategoriesApply(categories: Set<Category>) {
        savedStateHandle[CATEGORIES_KEY] = categories.map { it.name }
    }

    fun onCountrySelect(country: Country) {
        viewModelScope.launch { countryRepository.setCountry(country) }
    }

    private suspend fun loadArticles(
        country: Country,
        categories: Set<Category>,
        mode: ArticleListLoadState,
    ): ArticleListLoadState = when (
        val result = articleRepository.refreshTopHeadlines(
            country = country,
            categories = categories,
            forceRefresh = mode == ArticleListLoadState.Refreshing,
        )
    ) {
        RefreshResult.Success -> ArticleListLoadState.Idle

        is RefreshResult.Failure -> if (mode == ArticleListLoadState.Refreshing) {
            eventChannel.send(ArticleListEvent.ShowRefreshError(result.error))
            ArticleListLoadState.Idle
        } else {
            ArticleListLoadState.Failed(result.error)
        }
    }

    private companion object {

        const val CATEGORIES_KEY = "categories"
    }
}
