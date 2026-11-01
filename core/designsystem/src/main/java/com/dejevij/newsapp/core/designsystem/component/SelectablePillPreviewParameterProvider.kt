package com.dejevij.newsapp.core.designsystem.component

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

internal class SelectablePillPreviewParameterProvider :
    PreviewParameterProvider<SelectablePillPreviewState> {
    override val values = sequenceOf(
        SelectablePillPreviewState(text = "Sports", selected = true, enabled = true),
        SelectablePillPreviewState(text = "Sports", selected = false, enabled = true),
        SelectablePillPreviewState(text = "Sports", selected = false, enabled = false),
    )
}

internal data class SelectablePillPreviewState(
    val text: String,
    val selected: Boolean,
    val enabled: Boolean,
)
