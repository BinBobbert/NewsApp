package com.dejevij.newsapp.core.designsystem.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme

@Composable
fun <T> PreviewContainer(
    parameter: T,
    modifier: Modifier = Modifier,
    contentPadding: Dp? = null,
    content: @Composable (T) -> Unit,
) {
    NewsAppTheme {
        Surface(modifier = modifier) {
            Box(modifier = Modifier.padding(contentPadding ?: NewsAppTheme.spacing.default)) {
                content(parameter)
            }
        }
    }
}
