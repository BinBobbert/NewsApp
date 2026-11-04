package com.dejevij.newsapp.feature.articlelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dejevij.newsapp.core.designsystem.R as DesignSystemR
import com.dejevij.newsapp.core.designsystem.component.PrimaryButton
import com.dejevij.newsapp.core.designsystem.component.SecondaryButton
import com.dejevij.newsapp.core.designsystem.preview.PreviewContainer
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme
import com.dejevij.newsapp.core.model.Category
import com.dejevij.newsapp.core.model.Country
import com.dejevij.newsapp.core.ui.component.LoadingContent
import com.dejevij.newsapp.core.ui.component.MessageContent

@Composable
fun ArticleListScreen(
    uiState: ArticleListUiState,
    snackbarHostState: SnackbarHostState,
    onArticleClick: (url: String) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onCategoriesApply: (Set<Category>) -> Unit,
    onCountrySelect: (Country) -> Unit,
    modifier: Modifier = Modifier,
) {
    var openSheet by rememberSaveable { mutableStateOf<ArticleListSheet?>(null) }

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
        val layoutDirection = LocalLayoutDirection.current
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(layoutDirection),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(layoutDirection),
                )
                .fillMaxSize(),
        ) {
            ArticleListBody(
                uiState = uiState,
                bottomInset = innerPadding.calculateBottomPadding(),
                onArticleClick = onArticleClick,
                onRetry = onRetry,
                onClearFilters = { onCategoriesApply(emptySet()) },
                onChangeCountry = { openSheet = ArticleListSheet.Country },
            )
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
private fun ArticleListBody(
    uiState: ArticleListUiState,
    bottomInset: Dp,
    onArticleClick: (url: String) -> Unit,
    onRetry: () -> Unit,
    onClearFilters: () -> Unit,
    onChangeCountry: () -> Unit,
) {
    val insetModifier = Modifier.padding(bottom = bottomInset)
    when (val content = uiState.content) {
        ArticleListContent.Loading -> LoadingContent(modifier = insetModifier)

        ArticleListContent.Empty -> EmptyContent(
            countryName = uiState.country.name,
            hasCategoryFilter = uiState.selectedCategories.isNotEmpty(),
            onClearFilters = onClearFilters,
            onChangeCountry = onChangeCountry,
            modifier = insetModifier,
        )

        is ArticleListContent.Error -> MessageContent(
            message = stringResource(content.error.messageRes),
            modifier = insetModifier,
        ) {
            PrimaryButton(text = stringResource(R.string.retry), onClick = onRetry)
        }

        is ArticleListContent.Articles -> ArticleList(
            articles = content.articles,
            onArticleClick = onArticleClick,
            bottomInset = bottomInset,
        )
    }
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
            onApply = onCategoriesApply,
            onDismissRequest = { onSheetChange(null) },
        )

        ArticleListSheet.Country -> CountryPickerSheet(
            countries = uiState.countries,
            selectedCountry = uiState.country.country,
            onCountrySelect = onCountrySelect,
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
    modifier: Modifier = Modifier,
) {
    if (hasCategoryFilter) {
        MessageContent(
            message = stringResource(R.string.article_list_empty_filtered, countryName),
            modifier = modifier,
        ) {
            SecondaryButton(
                text = stringResource(R.string.article_list_clear_filters),
                onClick = onClearFilters,
            )
        }
    } else {
        MessageContent(
            message = stringResource(R.string.article_list_empty, countryName),
            modifier = modifier,
        ) {
            SecondaryButton(
                text = stringResource(R.string.article_list_change_country),
                onClick = onChangeCountry,
            )
        }
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
                        if (selectedCategoryCount > 0) {
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
private fun ArticleList(
    articles: List<ArticleViewData>,
    onArticleClick: (url: String) -> Unit,
    bottomInset: Dp,
    modifier: Modifier = Modifier,
) {
    val spacing = NewsAppTheme.spacing.default
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = spacing,
            top = spacing,
            end = spacing,
            bottom = spacing + bottomInset,
        ),
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
            snackbarHostState = remember { SnackbarHostState() },
            onArticleClick = {},
            onRefresh = {},
            onRetry = {},
            onCategoriesApply = {},
            onCountrySelect = {},
        )
    }
}

private enum class ArticleListSheet { Categories, Country }
