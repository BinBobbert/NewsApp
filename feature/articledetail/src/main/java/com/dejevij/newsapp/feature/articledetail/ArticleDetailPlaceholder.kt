package com.dejevij.newsapp.feature.articledetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme

@Composable
fun ArticleDetailPlaceholder(modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.padding(NewsAppTheme.spacing.large),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.article_detail_placeholder),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ArticleDetailPlaceholderPreview() {
    NewsAppTheme {
        ArticleDetailPlaceholder()
    }
}
