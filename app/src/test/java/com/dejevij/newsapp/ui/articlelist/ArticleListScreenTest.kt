package com.dejevij.newsapp.ui.articlelist

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.height
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dejevij.newsapp.R
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme
import com.dejevij.newsapp.data.DataError
import com.dejevij.newsapp.model.Category
import com.dejevij.newsapp.model.Country
import com.dejevij.newsapp.ui.displayName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticleListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val events = mutableListOf<String>()
    private val clickedUrls = mutableListOf<String>()

    @Test
    fun `loading shows a progress indicator`() {
        setContent(
            ArticleListUiState(content = ArticleListContent.Loading, country = Country.Default)
        )

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }

    @Test
    fun `articles are shown and clicking one reports its url`() {
        val article = previewArticle(url = "https://example.com/a", title = "Headline A")
        setContent(
            ArticleListUiState(
                content = ArticleListContent.Articles(listOf(article)),
                country = Country.Default
            )
        )

        composeTestRule.onNodeWithText("Headline A").performClick()

        assertEquals(listOf("https://example.com/a"), clickedUrls)
    }

    @Test
    fun `empty state names the country and offers to change it`() {
        setContent(
            ArticleListUiState(content = ArticleListContent.Empty, country = Country.Default)
        )

        composeTestRule.onNodeWithText(
            string(R.string.article_list_empty, Country.Default.displayName)
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.article_list_change_country)).performClick()

        assertEquals(listOf("country"), events)
    }

    @Test
    fun `empty state message is vertically centred`() {
        setContent(
            ArticleListUiState(content = ArticleListContent.Empty, country = Country.Default)
        )

        val root = composeTestRule.onRoot().getBoundsInRoot()
        val message = composeTestRule.onNodeWithText(
            string(R.string.article_list_empty, Country.Default.displayName)
        ).getBoundsInRoot()

        assertTrue(message.top > root.height / 3)
    }

    @Test
    fun `error state shows the error and retries`() {
        setContent(
            ArticleListUiState(
                content = ArticleListContent.Error(DataError.NoConnection),
                country = Country.Default
            )
        )

        composeTestRule.onNodeWithText(string(R.string.error_no_connection)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.retry)).performClick()

        assertEquals(listOf("retry"), events)
    }

    @Test
    fun `refresh error shows a snackbar that can retry`() {
        setContent(
            ArticleListUiState(
                content = ArticleListContent.Articles(listOf(previewArticle())),
                country = Country.Default,
                refreshError = DataError.RateLimited
            )
        )

        composeTestRule.onNodeWithText(string(R.string.error_rate_limited)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.retry)).performClick()
        composeTestRule.waitForIdle()

        assertEquals(listOf("retry", "errorDismiss"), events)
    }

    @Test
    fun `top bar actions report filter and country clicks`() {
        setContent(
            ArticleListUiState(
                content = ArticleListContent.Empty,
                country = Country.Default,
                selectedCategories = setOf(Category.SPORTS)
            )
        )

        composeTestRule.onNodeWithContentDescription(
            string(R.string.article_list_filter)
        ).performClick()
        composeTestRule.onNodeWithContentDescription(
            string(R.string.article_list_country, Country.Default.displayName)
        ).performClick()
        composeTestRule.onNodeWithText("1").assertIsDisplayed()

        assertEquals(listOf("filter", "country"), events)
    }

    private fun setContent(uiState: ArticleListUiState) {
        composeTestRule.setContent {
            NewsAppTheme {
                ArticleListScreen(
                    uiState = uiState,
                    onArticleClick = { clickedUrls += it },
                    onRefresh = { events += "refresh" },
                    onRetry = { events += "retry" },
                    onFilterClick = { events += "filter" },
                    onCountryClick = { events += "country" },
                    onRefreshErrorDismiss = { events += "errorDismiss" }
                )
            }
        }
    }

    private fun string(@StringRes id: Int, vararg args: Any) = context.getString(id, *args)
}
