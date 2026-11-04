package com.dejevij.newsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.dejevij.newsapp.feature.articledetail.ArticleDetailRoute
import com.dejevij.newsapp.feature.articlelist.ArticleListRoute

@Composable
fun NewsNavDisplay(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(ArticleListKey)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<ArticleListKey> {
                ArticleListRoute(onNavigateToArticle = { url ->
                    backStack.add(ArticleDetailKey(url))
                })
            }
            entry<ArticleDetailKey> { key ->
                ArticleDetailRoute(
                    url = key.url,
                    onNavigateBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
