package com.dejevij.newsapp.feature.articlelist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dejevij.newsapp.core.common.di.DefaultDispatcher
import com.dejevij.newsapp.core.data.ArticleRepository
import com.dejevij.newsapp.core.data.CountryRepository
import com.dejevij.newsapp.core.data.DataError
import com.dejevij.newsapp.core.data.RefreshResult
import com.dejevij.newsapp.core.model.Article
import com.dejevij.newsapp.core.model.Category
import com.dejevij.newsapp.core.model.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val countries = countryMapper.map(Country.all)
    private val refreshRequests = MutableSharedFlow<ArticleListLoadState>(extraBufferCapacity = 1)
    private val refreshError = MutableStateFlow<DataError?>(null)

    private val selectedCategories: Flow<Set<Category>> = savedStateHandle
        .getStateFlow(CATEGORIES_KEY, emptyList<String>())
        .map { names -> names.map(Category::valueOf).toSet() }

    // Everything below depends on the filters, so a filter change switches the whole pipeline
    // and stored articles of the previous filters are never shown with the new ones.
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ArticleListUiState> =
        combine(countryRepository.country, selectedCategories, ::Pair)
            .distinctUntilChanged()
            .flatMapLatest { (country, categories) ->
                combine(
                    articleRepository.topHeadlines(country, categories),
                    loadStates(country, categories),
                    refreshError,
                ) { articles, load, error ->
                    ArticleListUiState(
                        content = content(articles, load),
                        country = countries.first { it.country == country },
                        countries = countries,
                        selectedCategories = categories,
                        isRefreshing = load == ArticleListLoadState.Refreshing,
                        refreshError = error,
                    )
                }
            }
            .flowOn(defaultDispatcher)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = ArticleListUiState(
                    content = ArticleListContent.Loading,
                    country = countries.first {
                        it.country == Country.forLocale(Locale.getDefault())
                    },
                    countries = countries,
                ),
            )

    fun onRefresh() {
        refreshRequests.tryEmit(ArticleListLoadState.Refreshing)
    }

    fun onRetry() {
        val isShowingError = uiState.value.content is ArticleListContent.Error
        refreshRequests.tryEmit(
            if (isShowingError) ArticleListLoadState.Loading else ArticleListLoadState.Refreshing,
        )
    }

    fun onRefreshErrorShown() {
        refreshError.value = null
    }

    fun onCategoriesApply(categories: Set<Category>) {
        savedStateHandle[CATEGORIES_KEY] = categories.map { it.name }
    }

    fun onCountrySelect(country: Country) {
        viewModelScope.launch { countryRepository.setCountry(country) }
    }

    // Starts a load when collection starts, and a refresh for every refresh request.
    // transformLatest cancels a load that a newer request replaces.
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadStates(country: Country, categories: Set<Category>) = refreshRequests
        .onStart { emit(ArticleListLoadState.Loading) }
        .transformLatest { mode ->
            emit(mode)
            emit(loadArticles(country, categories, mode))
        }

    // Stored articles are shown whenever there are any, also while loading or after a failed load.
    private fun content(articles: List<Article>, load: ArticleListLoadState) = when {
        articles.isNotEmpty() -> ArticleListContent.Articles(articles.map(articleMapper::map))
        load == ArticleListLoadState.Loading -> ArticleListContent.Loading
        load is ArticleListLoadState.Failed -> ArticleListContent.Error(load.error)
        else -> ArticleListContent.Empty
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

        is RefreshResult.Failure -> if (
            mode == ArticleListLoadState.Refreshing ||
            articleRepository.topHeadlines(country, categories).first().isNotEmpty()
        ) {
            refreshError.value = result.error
            ArticleListLoadState.Idle
        } else {
            ArticleListLoadState.Failed(result.error)
        }
    }

    private companion object {

        const val CATEGORIES_KEY = "categories"
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
