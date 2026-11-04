package com.dejevij.newsapp.feature.articledetail

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.dejevij.newsapp.core.model.Article
import com.dejevij.newsapp.core.model.Country
import com.dejevij.newsapp.core.testing.FakeArticleRepository
import com.dejevij.newsapp.core.testing.FixedClock
import com.dejevij.newsapp.core.testing.MainDispatcherRule
import com.dejevij.newsapp.core.ui.RelativeTimeFormatter
import kotlin.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticleDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val articleRepository = FakeArticleRepository()

    @Test
    fun `shows the article with the matching url`() = runTest {
        cacheArticles(article("https://example.com/a"), article("https://example.com/b"))
        val viewModel = viewModel("https://example.com/b")
        collectUiState(viewModel)

        val content = viewModel.uiState.value as ArticleDetailUiState.Content
        assertEquals("https://example.com/b", content.article.url)
    }

    @Test
    fun `shows not available when the article is not cached`() = runTest {
        val viewModel = viewModel("https://example.com/missing")
        collectUiState(viewModel)

        assertEquals(ArticleDetailUiState.NotAvailable, viewModel.uiState.value)
    }

    @Test
    fun `read full article sends an open event with the url`() = runTest {
        val viewModel = viewModel("https://example.com/a")

        viewModel.events.test {
            viewModel.onReadFullArticleClick()

            assertEquals(ArticleDetailEvent.OpenArticle("https://example.com/a"), awaitItem())
        }
    }

    @Test
    fun `back sends a navigate back event`() = runTest {
        val viewModel = viewModel("https://example.com/a")

        viewModel.events.test {
            viewModel.onBackClick()

            assertEquals(ArticleDetailEvent.NavigateBack, awaitItem())
        }
    }

    private suspend fun cacheArticles(vararg articles: Article) {
        articleRepository.nextArticles = articles.toList()
        articleRepository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)
    }

    private fun viewModel(url: String) = ArticleDetailViewModel(
        url = url,
        articleRepository = articleRepository,
        mapper = ArticleDetailViewDataMapper(
            RelativeTimeFormatter(FixedClock(Instant.parse("2026-11-01T12:00:00Z"))),
        ),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.collectUiState(viewModel: ArticleDetailViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    private fun article(url: String) = Article(
        url = url,
        title = "Headline",
        sourceName = "BBC News",
        author = null,
        description = null,
        content = null,
        imageUrl = null,
        publishedAt = Instant.parse("2026-11-01T10:00:00Z"),
    )
}
