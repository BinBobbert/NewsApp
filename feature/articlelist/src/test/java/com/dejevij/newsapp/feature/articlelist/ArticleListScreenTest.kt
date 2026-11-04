package com.dejevij.newsapp.feature.articlelist

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
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
import com.dejevij.newsapp.core.data.DataError
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme
import com.dejevij.newsapp.core.model.Category
import com.dejevij.newsapp.core.model.Country
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
    private val unitedStates = previewCountry("us", "United States", "🇺🇸")
    private val netherlands = previewCountry("nl", "Netherlands", "🇳🇱")
    private val events = mutableListOf<String>()
    private val clickedUrls = mutableListOf<String>()
    private val appliedCategories = mutableListOf<Set<Category>>()
    private val selectedCountries = mutableListOf<Country>()

    @Test
    fun `loading shows a progress indicator`() {
        setContent(uiState(ArticleListContent.Loading))

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertIsDisplayed()
    }

    @Test
    fun `articles are shown and clicking one reports its url`() {
        val article = previewArticle(url = "https://example.com/a", title = "Headline A")
        setContent(uiState(ArticleListContent.Articles(listOf(article))))

        composeTestRule.onNodeWithText("Headline A").performClick()

        assertEquals(listOf("https://example.com/a"), clickedUrls)
    }

    @Test
    fun `empty state offers to change country through the picker`() {
        setContent(uiState(ArticleListContent.Empty))

        composeTestRule.onNodeWithText(string(R.string.article_list_empty, unitedStates.name))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.article_list_change_country)).performClick()
        composeTestRule.onNodeWithText("Netherlands").performClick()

        assertEquals(listOf(netherlands.country), selectedCountries)
    }

    @Test
    fun `empty state with category filters offers to clear them`() {
        setContent(uiState(ArticleListContent.Empty, selectedCategories = setOf(Category.SCIENCE)))

        composeTestRule.onNodeWithText(
            string(R.string.article_list_empty_filtered, unitedStates.name),
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.article_list_clear_filters)).performClick()

        assertEquals(listOf(emptySet<Category>()), appliedCategories)
    }

    @Test
    fun `empty state message is vertically centred`() {
        setContent(uiState(ArticleListContent.Empty))

        val root = composeTestRule.onRoot().getBoundsInRoot()
        val message = composeTestRule.onNodeWithText(
            string(R.string.article_list_empty, unitedStates.name),
        ).getBoundsInRoot()

        assertTrue(message.top > root.height / 3)
    }

    @Test
    fun `error state shows the error and retries`() {
        setContent(uiState(ArticleListContent.Error(DataError.NoConnection)))

        composeTestRule.onNodeWithText(string(R.string.error_no_connection)).assertIsDisplayed()
        composeTestRule.onNodeWithText(string(R.string.retry)).performClick()

        assertEquals(listOf("retry"), events)
    }

    @Test
    fun `filter sheet applies the selected categories`() {
        setContent(uiState(ArticleListContent.Empty, selectedCategories = setOf(Category.SPORTS)))

        composeTestRule.onNodeWithText("1").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription(string(R.string.article_list_filter))
            .performClick()
        composeTestRule.onNodeWithText(string(R.string.category_sports)).assertIsSelected()
        composeTestRule.onNodeWithText(string(R.string.category_technology)).performClick()
        composeTestRule.onNodeWithText(string(R.string.category_filter_apply)).performClick()

        assertEquals(listOf(setOf(Category.SPORTS, Category.TECHNOLOGY)), appliedCategories)
    }

    @Test
    fun `filter sheet clear removes all categories`() {
        setContent(uiState(ArticleListContent.Empty, selectedCategories = setOf(Category.SPORTS)))

        composeTestRule.onNodeWithContentDescription(string(R.string.article_list_filter))
            .performClick()
        composeTestRule.onNodeWithText(string(R.string.category_filter_clear)).performClick()
        composeTestRule.onNodeWithText(string(R.string.category_filter_clear)).assertIsNotEnabled()
        composeTestRule.onNodeWithText(string(R.string.category_filter_apply)).performClick()

        assertEquals(listOf(emptySet<Category>()), appliedCategories)
    }

    @Test
    fun `country action opens the picker with the current country selected`() {
        setContent(uiState(ArticleListContent.Empty))

        composeTestRule.onNodeWithContentDescription(
            string(R.string.article_list_country, unitedStates.name),
        ).performClick()
        composeTestRule.onNodeWithText("United States").assertIsSelected()
        composeTestRule.onNodeWithText("Netherlands").performClick()

        assertEquals(listOf(netherlands.country), selectedCountries)
    }

    private fun uiState(
        content: ArticleListContent,
        selectedCategories: Set<Category> = emptySet(),
    ) = ArticleListUiState(
        content = content,
        country = unitedStates,
        countries = listOf(netherlands, unitedStates),
        selectedCategories = selectedCategories,
    )

    private fun setContent(uiState: ArticleListUiState) {
        composeTestRule.setContent {
            NewsAppTheme {
                ArticleListScreen(
                    uiState = uiState,
                    snackbarHostState = remember { SnackbarHostState() },
                    onArticleClick = { clickedUrls += it },
                    onRefresh = { events += "refresh" },
                    onRetry = { events += "retry" },
                    onCategoriesApply = { appliedCategories += it },
                    onCountrySelect = { selectedCountries += it },
                )
            }
        }
    }

    private fun string(@StringRes id: Int, vararg args: Any) = context.getString(id, *args)
}
