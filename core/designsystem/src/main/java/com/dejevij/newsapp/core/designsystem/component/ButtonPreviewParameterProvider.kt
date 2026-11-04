package com.dejevij.newsapp.core.designsystem.component

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

internal class ButtonPreviewParameterProvider : PreviewParameterProvider<ButtonPreviewState> {
    override val values = sequenceOf(
        ButtonPreviewState(text = "Read full article", enabled = true),
        ButtonPreviewState(text = "Read full article", enabled = false),
    )
}

internal data class ButtonPreviewState(
    val text: String,
    val enabled: Boolean,
)
