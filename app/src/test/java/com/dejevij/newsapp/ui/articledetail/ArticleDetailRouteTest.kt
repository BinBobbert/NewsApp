package com.dejevij.newsapp.ui.articledetail

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dejevij.newsapp.R
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme
import com.dejevij.newsapp.model.Article
import com.dejevij.newsapp.model.Country
import com.dejevij.newsapp.testing.FakeArticleRepository
import com.dejevij.newsapp.testing.FixedClock
import com.dejevij.newsapp.ui.RelativeTimeFormatter
import kotlin.time.Instant
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class ArticleDetailRouteTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val articleRepository = FakeArticleRepository()
    private var navigatedBack = false

    @Test
    fun `read full article opens the url in a custom tab`() {
        setRoute()

        composeTestRule.onNodeWithText(string(R.string.article_detail_read_full_article))
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()

        val intent = shadowOf(composeTestRule.activity).nextStartedActivity
        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(ARTICLE_URL, intent.dataString)
    }

    @Test
    fun `read full article without a browser shows a message`() {
        shadowOf(RuntimeEnvironment.getApplication()).checkActivities(true)
        setRoute()

        composeTestRule.onNodeWithText(string(R.string.article_detail_read_full_article))
            .performScrollTo()
            .performClick()

        composeTestRule.waitUntil {
            composeTestRule.onAllNodesWithText(string(R.string.error_no_browser))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun `back navigates back`() {
        setRoute()

        composeTestRule.onNodeWithContentDescription(string(R.string.article_detail_back))
            .performClick()
        composeTestRule.waitForIdle()

        assertEquals(true, navigatedBack)
    }

    private fun setRoute() {
        runTest {
            articleRepository.nextArticles = listOf(article())
            articleRepository.refreshTopHeadlines(Country.Default, emptySet(), forceRefresh = false)
        }
        val viewModel = ArticleDetailViewModel(
            url = ARTICLE_URL,
            articleRepository = articleRepository,
            mapper = ArticleDetailViewDataMapper(
                RelativeTimeFormatter(FixedClock(Instant.parse("2026-11-01T12:00:00Z"))),
            ),
        )
        composeTestRule.setContent {
            NewsAppTheme {
                ArticleDetailRoute(
                    url = ARTICLE_URL,
                    onNavigateBack = { navigatedBack = true },
                    viewModel = viewModel,
                )
            }
        }
    }

    private fun string(id: Int) = composeTestRule.activity.getString(id)

    private fun article() = Article(
        url = ARTICLE_URL,
        title = "Headline",
        sourceName = "BBC News",
        author = null,
        description = null,
        content = null,
        imageUrl = null,
        publishedAt = Instant.parse("2026-11-01T10:00:00Z"),
    )

    private companion object {

        const val ARTICLE_URL = "https://example.com/article"
    }
}
