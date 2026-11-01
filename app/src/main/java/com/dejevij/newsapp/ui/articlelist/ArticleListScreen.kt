package com.dejevij.newsapp.ui.articlelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.dejevij.newsapp.R
import com.dejevij.newsapp.core.designsystem.R as DesignSystemR
import com.dejevij.newsapp.core.designsystem.component.PrimaryButton
import com.dejevij.newsapp.core.designsystem.component.SecondaryButton
import com.dejevij.newsapp.core.designsystem.preview.PreviewContainer
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme
import com.dejevij.newsapp.data.DataError
import com.dejevij.newsapp.model.Category
import com.dejevij.newsapp.model.Country
import com.dejevij.newsapp.ui.messageRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListScreen(
    uiState: ArticleListUiState,
    onArticleClick: (url: String) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onCategoriesApply: (Set<Category>) -> Unit,
    onCountrySelect: (Country) -> Unit,
    onRefreshErrorDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var openSheet by rememberSaveable { mutableStateOf<ArticleListSheet?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    RefreshErrorSnackbar(
        refreshError = uiState.refreshError,
        snackbarHostState = snackbarHostState,
        onRetry = onRetry,
        onDismiss = onRefreshErrorDismiss,
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            ArticleListTopBar(
                country = uiState.country,
                selectedCategoryCount = uiState.selectedCategories.size,
                onFilterClick = { openSheet = ArticleListSheet.Categories },
                onCountryClick = { openSheet = ArticleListSheet.Country },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            when (val content = uiState.content) {
                ArticleListContent.Loading -> LoadingContent()

                ArticleListContent.Empty -> EmptyContent(
                    countryName = uiState.country.name,
                    hasCategoryFilter = uiState.selectedCategories.isNotEmpty(),
                    onClearFilters = { onCategoriesApply(emptySet()) },
                    onChangeCountry = { openSheet = ArticleListSheet.Country },
                )

                is ArticleListContent.Error -> MessageContent(
                    message = stringResource(content.error.messageRes),
                ) {
                    PrimaryButton(text = stringResource(R.string.retry), onClick = onRetry)
                }

                is ArticleListContent.Articles -> ArticleList(
                    articles = content.articles,
                    onArticleClick = onArticleClick,
                )
            }
        }
    }

    ArticleListSheets(
        openSheet = openSheet,
        uiState = uiState,
        onCategoriesApply = onCategoriesApply,
        onCountrySelect = onCountrySelect,
        onSheetChange = { openSheet = it },
    )
}

@Composable
private fun ArticleListSheets(
    openSheet: ArticleListSheet?,
    uiState: ArticleListUiState,
    onCategoriesApply: (Set<Category>) -> Unit,
    onCountrySelect: (Country) -> Unit,
    onSheetChange: (ArticleListSheet?) -> Unit,
) {
    when (openSheet) {
        ArticleListSheet.Categories -> CategoryFilterSheet(
            selectedCategories = uiState.selectedCategories,
            onApply = { categories ->
                onSheetChange(null)
                onCategoriesApply(categories)
            },
            onDismissRequest = { onSheetChange(null) },
        )

        ArticleListSheet.Country -> CountryPickerSheet(
            countries = uiState.countries,
            selectedCountry = uiState.country.country,
            onCountrySelect = { country ->
                onSheetChange(null)
                onCountrySelect(country)
            },
            onDismissRequest = { onSheetChange(null) },
        )

        null -> Unit
    }
}

@Composable
private fun EmptyContent(
    countryName: String,
    hasCategoryFilter: Boolean,
    onClearFilters: () -> Unit,
    onChangeCountry: () -> Unit,
) {
    if (hasCategoryFilter) {
        MessageContent(
            message = stringResource(R.string.article_list_empty_filtered, countryName),
        ) {
            SecondaryButton(
                text = stringResource(R.string.article_list_clear_filters),
                onClick = onClearFilters,
            )
        }
    } else {
        MessageContent(message = stringResource(R.string.article_list_empty, countryName)) {
            SecondaryButton(
                text = stringResource(R.string.article_list_change_country),
                onClick = onChangeCountry,
            )
        }
    }
}

@Composable
private fun RefreshErrorSnackbar(
    refreshError: DataError?,
    snackbarHostState: SnackbarHostState,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (refreshError == null) return
    val message = stringResource(refreshError.messageRes)
    val actionLabel = stringResource(R.string.retry)
    val currentOnRetry by rememberUpdatedState(onRetry)
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    LaunchedEffect(refreshError) {
        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Long,
        )
        if (result == SnackbarResult.ActionPerformed) currentOnRetry()
        currentOnDismiss()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArticleListTopBar(
    country: CountryViewData,
    selectedCategoryCount: Int,
    onFilterClick: () -> Unit,
    onCountryClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.article_list_title)) },
        actions = {
            val countryDescription =
                stringResource(R.string.article_list_country, country.name)
            IconButton(
                onClick = onCountryClick,
                modifier = Modifier.semantics { contentDescription = countryDescription },
            ) {
                Text(
                    text = country.flag,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.clearAndSetSemantics {},
                )
            }
            IconButton(onClick = onFilterClick) {
                BadgedBox(
                    badge = {
                        if (selectedCategoryCount >
                            0
                        ) {
                            Badge { Text(text = "$selectedCategoryCount") }
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(DesignSystemR.drawable.ic_filter_list),
                        contentDescription = stringResource(R.string.article_list_filter),
                    )
                }
            }
        },
    )
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MessageContent(
    message: String,
    modifier: Modifier = Modifier,
    action: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(NewsAppTheme.spacing.large),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(NewsAppTheme.spacing.default))
        action()
    }
}

@Composable
private fun ArticleList(
    articles: List<ArticleViewData>,
    onArticleClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(NewsAppTheme.spacing.default),
        verticalArrangement = Arrangement.spacedBy(NewsAppTheme.spacing.default),
    ) {
        items(items = articles, key = { it.url }) { article ->
            ArticleCard(article = article, onClick = { onArticleClick(article.url) })
        }
    }
}

@PreviewLightDark
@Composable
private fun ArticleListScreenPreview(
    @PreviewParameter(ArticleListScreenPreviewParameterProvider::class) uiState: ArticleListUiState,
) {
    PreviewContainer(uiState, contentPadding = 0.dp) {
        ArticleListScreen(
            uiState = it,
            onArticleClick = {},
            onRefresh = {},
            onRetry = {},
            onCategoriesApply = {},
            onCountrySelect = {},
            onRefreshErrorDismiss = {},
        )
    }
}

private enum class ArticleListSheet { Categories, Country }
