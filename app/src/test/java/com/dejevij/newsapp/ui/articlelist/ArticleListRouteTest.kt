package com.dejevij.newsapp.ui.articlelist

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dejevij.newsapp.R
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme
import com.dejevij.newsapp.data.DataError
import com.dejevij.newsapp.data.RefreshResult
import com.dejevij.newsapp.model.Article
import com.dejevij.newsapp.testing.FakeArticleRepository
import com.dejevij.newsapp.testing.FakeCountryRepository
import com.dejevij.newsapp.testing.FixedClock
import com.dejevij.newsapp.ui.RelativeTimeFormatter
import kotlin.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticleListRouteTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val articleRepository = FakeArticleRepository().apply {
        nextArticles = listOf(article("https://example.com/a"))
    }
    private val navigatedUrls = mutableListOf<String>()

    @Test
    fun `clicking an article navigates to it`() {
        setRoute()

        composeTestRule.onNodeWithText("Headline").performClick()
        composeTestRule.waitForIdle()

        assertEquals(listOf("https://example.com/a"), navigatedUrls)
    }

    @Test
    fun `a failed refresh shows a snackbar whose retry refreshes again`() {
        val viewModel = setRoute()
        articleRepository.nextResult = RefreshResult.Failure(DataError.RateLimited)

        composeTestRule.runOnIdle { viewModel.onRefresh() }
        composeTestRule.waitUntil {
            composeTestRule.onAllNodesWithText(context.getString(R.string.error_rate_limited))
                .fetchSemanticsNodes().isNotEmpty()
        }
        val requestsBeforeRetry = articleRepository.requests.size
        composeTestRule.onNodeWithText(context.getString(R.string.retry)).performClick()
        composeTestRule.waitForIdle()

        assertEquals(requestsBeforeRetry + 1, articleRepository.requests.size)
    }

    private fun setRoute(): ArticleListViewModel {
        val viewModel = ArticleListViewModel(
            articleRepository = articleRepository,
            countryRepository = FakeCountryRepository(),
            articleMapper = ArticleViewDataMapper(
                RelativeTimeFormatter(FixedClock(Instant.parse("2026-11-01T12:00:00Z"))),
            ),
            countryMapper = CountryViewDataMapper(),
            savedStateHandle = SavedStateHandle(),
        )
        composeTestRule.setContent {
            NewsAppTheme {
                ArticleListRoute(
                    onNavigateToArticle = { navigatedUrls += it },
                    viewModel = viewModel,
                )
            }
        }
        return viewModel
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
