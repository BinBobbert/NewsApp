package com.dejevij.newsapp.feature.articlelist

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ArticleListRoute(
    onNavigateToArticle: (url: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArticleListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    uiState.refreshError?.let { error ->
        LaunchedEffect(error) {
            val result = snackbarHostState.showSnackbar(
                message = resources.getString(error.messageRes),
                actionLabel = resources.getString(R.string.retry),
                duration = SnackbarDuration.Long,
            )
            viewModel.onRefreshErrorShown()
            if (result == SnackbarResult.ActionPerformed) viewModel.onRetry()
        }
    }

    ArticleListScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onArticleClick = onNavigateToArticle,
        onRefresh = viewModel::onRefresh,
        onRetry = viewModel::onRetry,
        onCategoriesApply = viewModel::onCategoriesApply,
        onCountrySelect = viewModel::onCountrySelect,
        modifier = modifier,
    )
}
