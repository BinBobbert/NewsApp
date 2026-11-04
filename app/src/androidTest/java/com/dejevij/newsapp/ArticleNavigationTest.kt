package com.dejevij.newsapp

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import com.dejevij.newsapp.feature.articledetail.R as ArticleDetailR
import com.dejevij.newsapp.feature.articlelist.R as ArticleListR
import com.dejevij.newsapp.testing.testArticle
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ArticleNavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        Intents.init()
        intending(
            hasAction(Intent.ACTION_VIEW),
        ).respondWith(ActivityResult(Activity.RESULT_OK, null))
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun openingAnArticleShowsTheDetailAndBackReturnsToTheList() {
        waitForText(testArticle.title)
        composeTestRule.onNodeWithText(testArticle.title).performClick()

        waitForText(testArticle.description!!)
        composeTestRule.onNodeWithText(
            string(ArticleDetailR.string.article_detail_read_full_article),
        )
            .performScrollTo()
            .performClick()
        intended(allOf(hasAction(Intent.ACTION_VIEW), hasData(testArticle.url)))

        pressBack()
        waitForText(string(ArticleListR.string.article_list_title))
        composeTestRule.onNodeWithText(
            string(ArticleDetailR.string.article_detail_read_full_article),
        )
            .assertDoesNotExist()
    }

    private fun waitForText(text: String) {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun string(id: Int) = composeTestRule.activity.getString(id)
}
