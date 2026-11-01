package com.dejevij.newsapp.core.designsystem.component

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.dejevij.newsapp.core.designsystem.preview.PreviewContainer

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
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
    enabled: Boolean = true,
) {
    OutlinedButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        Text(text = text)
    }
}

@PreviewLightDark
@Composable
private fun PrimaryButtonPreview(
    @PreviewParameter(ButtonPreviewParameterProvider::class) state: ButtonPreviewState,
) {
    PreviewContainer(state) {
        PrimaryButton(text = it.text, onClick = {}, enabled = it.enabled)
    }
}

@PreviewLightDark
@Composable
private fun SecondaryButtonPreview(
    @PreviewParameter(ButtonPreviewParameterProvider::class) state: ButtonPreviewState,
) {
    PreviewContainer(state) {
        SecondaryButton(text = it.text, onClick = {}, enabled = it.enabled)
    }
}
