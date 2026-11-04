package com.dejevij.newsapp.feature.articlelist

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dejevij.newsapp.core.ui.util.ObserveAsEvents
import kotlinx.coroutines.launch

@Composable
fun ArticleListRoute(
    onNavigateToArticle: (url: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArticleListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val resources = LocalResources.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ArticleListEvent.NavigateToArticle -> onNavigateToArticle(event.url)

            is ArticleListEvent.ShowRefreshError -> scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = resources.getString(event.error.messageRes),
                    actionLabel = resources.getString(R.string.retry),
                    duration = SnackbarDuration.Long,
                )
                if (result == SnackbarResult.ActionPerformed) viewModel.onRetry()
            }
        }
    }

    ArticleListScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onArticleClick = viewModel::onArticleClick,
        onRefresh = viewModel::onRefresh,
        onRetry = viewModel::onRetry,
        onCategoriesApply = viewModel::onCategoriesApply,
        onCountrySelect = viewModel::onCountrySelect,
        modifier = modifier,
    )
}
