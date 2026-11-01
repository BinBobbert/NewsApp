package com.dejevij.newsapp.core.designsystem.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
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
class PillTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `pill shows its text and is not clickable`() {
        composeTestRule.setContent {
            NewsAppTheme { Pill(text = "BBC News") }
        }

        composeTestRule.onNodeWithText("BBC News").assertIsDisplayed().assertHasNoClickAction()
    }

    @Test
    fun `selectable pill toggles its selected state when clicked`() {
        composeTestRule.setContent {
            NewsAppTheme {
                var selected by remember { mutableStateOf(false) }
                SelectablePill(
                    text = "Sports",
                    selected = selected,
                    onClick = { selected = !selected },
                )
            }
        }
        val pill = composeTestRule.onNodeWithText("Sports")

        pill.assertIsNotSelected()
        pill.performClick()
        pill.assertIsSelected()
    }

    @Test
    fun `disabled selectable pill ignores clicks`() {
        var clicks = 0
        composeTestRule.setContent {
            NewsAppTheme {
                SelectablePill(
                    text = "Sports",
                    selected = false,
                    onClick = { clicks++ },
                    enabled = false,
                )
            }
        }

        composeTestRule.onNodeWithText("Sports").assertIsNotEnabled().performClick()

        assertEquals(0, clicks)
    }
}
