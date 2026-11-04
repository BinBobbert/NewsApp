package com.dejevij.newsapp.ui.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.dejevij.newsapp.feature.articledetail.ArticleDetailPlaceholder
import com.dejevij.newsapp.feature.articledetail.ArticleDetailRoute
import com.dejevij.newsapp.feature.articlelist.ArticleListRoute

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun NewsNavDisplay(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(ArticleListKey)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(
        backNavigationBehavior = BackNavigationBehavior.PopUntilContentChange,
    )

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        modifier = modifier,
        sceneStrategies = listOf(listDetailStrategy),
        transitionSpec = { pushTransform() },
        popTransitionSpec = { popTransform() },
        predictivePopTransitionSpec = { popTransform() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<ArticleListKey>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = { ArticleDetailPlaceholder() },
                ),
            ) {
                ArticleListRoute(onNavigateToArticle = { url ->
                    // With both panes visible, a new article replaces the open one, so back
                    // returns to the list instead of to every previously opened article.
                    backStack.removeAll { it is ArticleDetailKey }
                    backStack.add(ArticleDetailKey(url))
                })
            }
            entry<ArticleDetailKey>(metadata = ListDetailSceneStrategy.detailPane()) { key ->
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
