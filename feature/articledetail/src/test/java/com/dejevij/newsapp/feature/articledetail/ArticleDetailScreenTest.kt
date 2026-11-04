package com.dejevij.newsapp.feature.articledetail

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticleDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val events = mutableListOf<String>()

    @Test
    fun `content shows all article fields`() {
        setContent(ArticleDetailUiState.Content(previewArticleDetail(imageUrl = null)))

        composeTestRule.onNodeWithText("BBC News").assertIsDisplayed()
        composeTestRule.onNodeWithText("New chip promises week-long phone battery life")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.article_detail_author, "Jane Doe"))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("2 hours ago").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "Researchers say the design cuts idle power use by 80 percent.",
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "The prototype, unveiled on Monday, combines a new low-power core with…",
        ).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `missing optional fields are left out`() {
        setContent(
            ArticleDetailUiState.Content(
                previewArticleDetail(author = null, description = null, content = null),
            ),
        )

        composeTestRule.onNodeWithText(string(R.string.article_detail_author, "Jane Doe"))
            .assertDoesNotExist()
        composeTestRule.onNodeWithText(
            "Researchers say the design cuts idle power use by 80 percent.",
        ).assertDoesNotExist()
    }

    @Test
    fun `read full article reports a click`() {
        setContent(
            ArticleDetailUiState.Content(previewArticleDetail(url = "https://example.com/a")),
        )

        composeTestRule.onNodeWithText(string(R.string.article_detail_read_full_article))
            .performScrollTo()
            .performClick()

        assertEquals(listOf("open"), events)
    }

    @Test
    fun `back arrow reports a back click`() {
        setContent(ArticleDetailUiState.Content(previewArticleDetail()))

        composeTestRule.onNodeWithContentDescription(string(R.string.article_detail_back))
            .performClick()

        assertEquals(listOf("back"), events)
    }

    @Test
    fun `not available offers to go back to the headlines`() {
        setContent(ArticleDetailUiState.NotAvailable)

        composeTestRule.onNodeWithText(string(R.string.article_detail_not_available))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.article_detail_back_to_headlines))
            .performClick()

        assertEquals(listOf("back"), events)
    }

    @Test
    fun `loading shows a progress indicator`() {
        setContent(ArticleDetailUiState.Loading)

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }

    private fun setContent(uiState: ArticleDetailUiState) {
        composeTestRule.setContent {
            NewsAppTheme {
                ArticleDetailScreen(
                    uiState = uiState,
                    snackbarHostState = remember { SnackbarHostState() },
                    onBackClick = { events += "back" },
                    onReadFullArticleClick = { events += "open" },
                )
            }
        }
    }

    private fun string(@StringRes id: Int, vararg args: Any) = context.getString(id, *args)
}
