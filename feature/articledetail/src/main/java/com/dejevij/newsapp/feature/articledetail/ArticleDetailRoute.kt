package com.dejevij.newsapp.feature.articledetail

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dejevij.newsapp.core.ui.util.ObserveAsEvents
import com.dejevij.newsapp.core.ui.util.openInCustomTab
import kotlinx.coroutines.launch

@Composable
fun ArticleDetailRoute(
    url: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArticleDetailViewModel =
        hiltViewModel<ArticleDetailViewModel, ArticleDetailViewModel.Factory>(
            key = url,
        ) { factory ->
            factory.create(url)
        },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val resources = LocalResources.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is ArticleDetailEvent.OpenArticle ->
                if (!context.openInCustomTab(event.url, toolbarColor)) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            resources.getString(R.string.error_no_browser),
                        )
                    }
                }

            ArticleDetailEvent.NavigateBack -> onNavigateBack()
        }
    }

    ArticleDetailScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = viewModel::onBackClick,
        onReadFullArticleClick = viewModel::onReadFullArticleClick,
        modifier = modifier,
    )
}
