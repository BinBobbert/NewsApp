package com.dejevij.newsapp.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.dejevij.newsapp.core.designsystem.preview.PreviewContainer
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme

@Composable
fun Pill(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = NewsAppTheme.spacing.small,
                vertical = NewsAppTheme.spacing.tiny,
            ),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun SelectablePill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = text) },
        modifier = modifier,
        enabled = enabled,
        shape = CircleShape,
    )
}

@PreviewLightDark
@Composable
private fun PillPreview(
    @PreviewParameter(PillPreviewParameterProvider::class) state: PillPreviewState,
) {
    PreviewContainer(state) {
        Pill(text = it.text)
    }
}

@PreviewLightDark
@Composable
private fun SelectablePillPreview(
    @PreviewParameter(SelectablePillPreviewParameterProvider::class) state:
    SelectablePillPreviewState,
) {
    PreviewContainer(state) {
        SelectablePill(text = it.text, selected = it.selected, onClick = {}, enabled = it.enabled)
    }
}
