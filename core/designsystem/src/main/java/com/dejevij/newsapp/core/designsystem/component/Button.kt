package com.dejevij.newsapp.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(onClick = onClick, modifier = modifier, enabled = enabled) {
        Text(text = text)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        Text(text = text)
    }
}

@PreviewLightDark
@Composable
private fun ButtonPreview() {
    NewsAppTheme {
        Surface {
            Column(
                modifier = Modifier.padding(NewsAppTheme.spacing.default),
                verticalArrangement = Arrangement.spacedBy(NewsAppTheme.spacing.small)
            ) {
                PrimaryButton(text = "Read full article", onClick = {})
                SecondaryButton(text = "Retry", onClick = {})
                PrimaryButton(text = "Disabled", onClick = {}, enabled = false)
                SecondaryButton(text = "Disabled", onClick = {}, enabled = false)
            }
        }
    }
}
