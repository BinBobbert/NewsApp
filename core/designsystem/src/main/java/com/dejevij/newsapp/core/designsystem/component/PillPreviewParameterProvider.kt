package com.dejevij.newsapp.core.designsystem.component

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

internal class PillPreviewParameterProvider : PreviewParameterProvider<PillPreviewState> {
    override val values = sequenceOf(
        PillPreviewState(text = "BBC News"),
        PillPreviewState(text = "A very long source name that does not fit on one line"),
    )
}

internal data class PillPreviewState(val text: String)
