package com.dejevij.newsapp.ui.articlelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import coil3.compose.AsyncImage
import com.dejevij.newsapp.core.designsystem.component.Pill
import com.dejevij.newsapp.core.designsystem.preview.PreviewContainer
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme
import com.dejevij.newsapp.ui.component.ArticleImage

@Composable
fun ArticleCard(article: ArticleViewData, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        if (article.imageUrl != null) {
            ArticleImage(imageUrl = article.imageUrl)
        }
        Column(modifier = Modifier.padding(NewsAppTheme.spacing.default)) {
            Pill(text = article.sourceName)
            Spacer(modifier = Modifier.height(NewsAppTheme.spacing.small))
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            if (article.description != null) {
                Spacer(modifier = Modifier.height(NewsAppTheme.spacing.small))
                Text(
                    text = article.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(NewsAppTheme.spacing.small))
            Text(
                text = article.publishedAt,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ArticleCardPreview(
    @PreviewParameter(ArticleCardPreviewParameterProvider::class) article: ArticleViewData,
) {
    PreviewContainer(article) {
        ArticleCard(article = it, onClick = {})
    }
}
