package com.dejevij.newsapp.ui.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
        transitionSpec = { pushTransform() },
        popTransitionSpec = { popTransform() },
        predictivePopTransitionSpec = { popTransform() },
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

private fun pushTransform(): ContentTransform = slideInHorizontally { it } togetherWith
    slideOutHorizontally { -it / PARALLAX_DIVISOR } + fadeOut()

private fun popTransform(): ContentTransform =
    slideInHorizontally { -it / PARALLAX_DIVISOR } + fadeIn() togetherWith
        slideOutHorizontally { it }

private const val PARALLAX_DIVISOR = 4
