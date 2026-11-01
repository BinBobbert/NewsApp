package com.dejevij.newsapp.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme

@Composable
fun Pill(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = NewsAppTheme.spacing.small,
                vertical = NewsAppTheme.spacing.tiny
            ),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SelectablePill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = text) },
        modifier = modifier,
        enabled = enabled,
        shape = CircleShape
    )
}

@PreviewLightDark
@Composable
private fun PillPreview() {
    NewsAppTheme {
        Surface {
            Row(
                modifier = Modifier.padding(NewsAppTheme.spacing.default),
                horizontalArrangement = Arrangement.spacedBy(NewsAppTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Pill(text = "BBC News")
                SelectablePill(text = "Selected", selected = true, onClick = {})
                SelectablePill(text = "Unselected", selected = false, onClick = {})
            }
        }
    }
}
