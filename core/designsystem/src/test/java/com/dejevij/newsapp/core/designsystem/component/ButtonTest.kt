package com.dejevij.newsapp.core.designsystem.component

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `button shows its text and calls onClick when clicked`() {
        var clicks = 0
        composeTestRule.setContent {
            NewsAppTheme { PrimaryButton(text = "Read full article", onClick = { clicks++ }) }
        }

        composeTestRule.onNodeWithText("Read full article")
            .assertIsEnabled()
            .assertHasClickAction()
            .performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun `disabled button ignores clicks`() {
        var clicks = 0
        composeTestRule.setContent {
            NewsAppTheme { PrimaryButton(text = "Read", onClick = { clicks++ }, enabled = false) }
        }

        composeTestRule.onNodeWithText("Read").assertIsNotEnabled().performClick()

        assertEquals(0, clicks)
    }

    @Test
    fun `outlined button shows its text and calls onClick when clicked`() {
        var clicks = 0
        composeTestRule.setContent {
            NewsAppTheme { SecondaryButton(text = "Retry", onClick = { clicks++ }) }
        }

        composeTestRule.onNodeWithText("Retry").assertIsEnabled().performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun `disabled outlined button ignores clicks`() {
        var clicks = 0
        composeTestRule.setContent {
            NewsAppTheme {
                SecondaryButton(text = "Retry", onClick = { clicks++ }, enabled = false)
            }
        }

        composeTestRule.onNodeWithText("Retry").assertIsNotEnabled().performClick()

        assertEquals(0, clicks)
    }
}
