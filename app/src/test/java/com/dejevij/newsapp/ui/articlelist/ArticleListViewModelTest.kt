package com.dejevij.newsapp.ui.articlelist

import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.dejevij.newsapp.data.DataError
import com.dejevij.newsapp.data.RefreshResult
import com.dejevij.newsapp.model.Article
import com.dejevij.newsapp.model.Category
import com.dejevij.newsapp.model.Country
import com.dejevij.newsapp.testing.FakeArticleRepository
import com.dejevij.newsapp.testing.FakeCountryRepository
import com.dejevij.newsapp.testing.FixedClock
import com.dejevij.newsapp.testing.MainDispatcherRule
import kotlin.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticleListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val articleRepository = FakeArticleRepository()
    private val countryRepository = FakeCountryRepository()

    @Test
    fun `loads headlines for the current country on start`() = runTest {
        articleRepository.nextArticles = listOf(article("a"), article("b"))
        val viewModel = viewModel()
        collectUiState(viewModel)

        val content = viewModel.uiState.value.content as ArticleListContent.Articles
        assertEquals(listOf("a", "b"), content.articles.map { it.url })
        assertEquals(listOf(Country.Default to emptySet<Category>()), articleRepository.requests)
    }

    @Test
    fun `shows the empty state when there are no headlines`() = runTest {
        val viewModel = viewModel()
        collectUiState(viewModel)

        assertEquals(ArticleListContent.Empty, viewModel.uiState.value.content)
    }

    @Test
    fun `shows an error when the first load fails and recovers on retry`() = runTest {
        articleRepository.nextResult = RefreshResult.Failure(DataError.NoConnection)
        val viewModel = viewModel()
        collectUiState(viewModel)

        assertEquals(
            ArticleListContent.Error(DataError.NoConnection),
            viewModel.uiState.value.content,
        )

        articleRepository.nextResult = RefreshResult.Success
        articleRepository.nextArticles = listOf(article("a"))
        viewModel.onRetry()

        assertEquals(
            1,
            (viewModel.uiState.value.content as ArticleListContent.Articles).articles.size,
        )
    }

    @Test
    fun `failed pull to refresh keeps the articles and sends an error event`() = runTest {
        articleRepository.nextArticles = listOf(article("a"))
        val viewModel = viewModel()
        collectUiState(viewModel)

        viewModel.events.test {
            articleRepository.nextResult = RefreshResult.Failure(DataError.RateLimited)
            viewModel.onRefresh()

            assertEquals(ArticleListEvent.ShowRefreshError(DataError.RateLimited), awaitItem())
        }
        assertEquals(listOf(false, true), articleRepository.forceRefreshes)
        assertEquals(
            1,
            (viewModel.uiState.value.content as ArticleListContent.Articles).articles.size,
        )
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun `applying categories reloads with those categories`() = runTest {
        val viewModel = viewModel()
        collectUiState(viewModel)

        viewModel.onCategoriesApply(setOf(Category.SPORTS))

        assertEquals(setOf(Category.SPORTS), viewModel.uiState.value.selectedCategories)
        assertEquals(Country.Default to setOf(Category.SPORTS), articleRepository.requests.last())
    }

    @Test
    fun `selecting a country stores it and reloads for that country`() = runTest {
        val viewModel = viewModel()
        collectUiState(viewModel)
        val japan = requireNotNull(Country.fromCode("jp"))

        viewModel.onCountrySelect(japan)

        assertEquals(japan, viewModel.uiState.value.country.country)
        assertEquals(japan to emptySet<Category>(), articleRepository.requests.last())
    }

    @Test
    fun `restores selected categories from saved state`() = runTest {
        val viewModel = viewModel(SavedStateHandle(mapOf("categories" to listOf("HEALTH"))))
        collectUiState(viewModel)

        assertEquals(Country.Default to setOf(Category.HEALTH), articleRepository.requests.single())
    }

    @Test
    fun `clicking an article sends an open article event`() = runTest {
        val viewModel = viewModel()

        viewModel.events.test {
            viewModel.onArticleClick("https://example.com/a")

            assertEquals(ArticleListEvent.OpenArticle("https://example.com/a"), awaitItem())
        }
    }

    private fun viewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) =
        ArticleListViewModel(
            articleRepository = articleRepository,
            countryRepository = countryRepository,
            articleMapper = ArticleViewDataMapper(
                FixedClock(Instant.parse("2026-11-01T12:00:00Z")),
            ),
            countryMapper = CountryViewDataMapper(),
            savedStateHandle = savedStateHandle,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.collectUiState(viewModel: ArticleListViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    private fun article(url: String) = Article(
        url = url,
        title = "Headline $url",
        sourceName = "BBC News",
        author = null,
        description = null,
        content = null,
        imageUrl = null,
        publishedAt = Instant.parse("2026-11-01T10:00:00Z"),
    )
}
