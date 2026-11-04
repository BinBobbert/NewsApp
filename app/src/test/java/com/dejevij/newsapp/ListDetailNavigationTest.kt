package com.dejevij.newsapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dejevij.newsapp.feature.articledetail.R as ArticleDetailR
import com.dejevij.newsapp.feature.articlelist.R as ArticleListR
import com.dejevij.newsapp.testing.otherTestArticle
import com.dejevij.newsapp.testing.testArticle
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, qualifiers = "w1280dp-h800dp-land")
class ListDetailNavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `shows the list next to a placeholder`() {
        waitForText(testArticle.title)

        composeTestRule.onNodeWithText(string(ArticleDetailR.string.article_detail_placeholder))
            .assertIsDisplayed()
    }

    @Test
    fun `opening an article shows it next to the list`() {
        waitForText(testArticle.title)

        composeTestRule.onNodeWithText(testArticle.title).performClick()

        waitForText(testArticle.description!!)
        composeTestRule.onNodeWithText(string(ArticleListR.string.article_list_title))
            .assertIsDisplayed()
    }

    @Test
    fun `back after opening a second article returns to the placeholder`() {
        waitForText(testArticle.title)
        composeTestRule.onNodeWithText(testArticle.title).performClick()
        waitForText(testArticle.description!!)
        composeTestRule.onNodeWithText(otherTestArticle.title).performClick()
        waitForText(otherTestArticle.description!!)

        composeTestRule.runOnUiThread {
            composeTestRule.activity.onBackPressedDispatcher.onBackPressed()
        }

        waitForText(string(ArticleDetailR.string.article_detail_placeholder))
        assertFalse(composeTestRule.activity.isFinishing)
    }

    private fun waitForText(text: String) {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun string(id: Int) = composeTestRule.activity.getString(id)
}
