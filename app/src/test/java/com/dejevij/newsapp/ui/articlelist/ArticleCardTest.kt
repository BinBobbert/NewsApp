package com.dejevij.newsapp.ui.articlelist

import android.content.Context
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.ColorImage
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import coil3.intercept.Interceptor
import coil3.request.ErrorResult
import coil3.test.FakeImageLoaderEngine
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(DelicateCoilApi::class)
@RunWith(AndroidJUnit4::class)
class ArticleCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        val engine = FakeImageLoaderEngine.Builder()
            .intercept({ data ->
                data.toString() == LOADING_IMAGE_URL
            }, ColorImage(color = 0xFF0000FF.toInt(), width = 160, height = 90))
            .default(
                Interceptor { chain ->
                    ErrorResult(
                        image = null,
                        request = chain.request,
                        throwable = IllegalStateException(),
                    )
                },
            )
            .build()
        val context = ApplicationProvider.getApplicationContext<Context>()
        SingletonImageLoader.setUnsafe(
            ImageLoader.Builder(context).components {
                add(engine)
            }.build(),
        )
    }

    @After
    fun tearDown() {
        SingletonImageLoader.reset()
    }

    @Test
    fun `an image that loads is shown above the text`() {
        setCard(imageUrl = LOADING_IMAGE_URL)

        composeTestRule.waitUntil { titleTop() > IMAGE_SHOWN_MIN_TOP }
    }

    @Test
    fun `an image that fails to load is hidden`() {
        setCard(imageUrl = "https://example.com/missing.jpg")

        composeTestRule.waitUntil { titleTop() < IMAGE_SHOWN_MIN_TOP }
    }

    private fun setCard(imageUrl: String) {
        composeTestRule.setContent {
            NewsAppTheme {
                ArticleCard(
                    article = previewArticle(
                        title = TITLE,
                        imageUrl = imageUrl,
                    ),
                    onClick = {},
                )
            }
        }
    }

    private fun titleTop() =
        composeTestRule.onNodeWithText(TITLE, useUnmergedTree = true).getBoundsInRoot().top

    private companion object {

        const val TITLE = "Headline"
        const val LOADING_IMAGE_URL = "https://example.com/image.jpg"
        val IMAGE_SHOWN_MIN_TOP = 120.dp
    }
}
