package com.dejevij.newsapp.ui.articledetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.dejevij.newsapp.R
import com.dejevij.newsapp.core.designsystem.R as DesignSystemR
import com.dejevij.newsapp.core.designsystem.component.Pill
import com.dejevij.newsapp.core.designsystem.component.PrimaryButton
import com.dejevij.newsapp.core.designsystem.component.SecondaryButton
import com.dejevij.newsapp.core.designsystem.preview.PreviewContainer
import com.dejevij.newsapp.core.designsystem.theme.NewsAppTheme
import com.dejevij.newsapp.ui.component.ArticleImage
import com.dejevij.newsapp.ui.component.LoadingContent
import com.dejevij.newsapp.ui.component.MessageContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    uiState: ArticleDetailUiState,
    onBackClick: () -> Unit,
    onReadFullArticleClick: (url: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(DesignSystemR.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.article_detail_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        when (uiState) {
            ArticleDetailUiState.Loading -> LoadingContent(modifier = contentModifier)

            ArticleDetailUiState.NotAvailable -> MessageContent(
                message = stringResource(R.string.article_detail_not_available),
                modifier = contentModifier,
            ) {
                SecondaryButton(
                    text = stringResource(R.string.article_detail_back_to_headlines),
                    onClick = onBackClick,
                )
            }

            is ArticleDetailUiState.Content -> ArticleDetailContent(
                article = uiState.article,
                onReadFullArticleClick = { onReadFullArticleClick(uiState.article.url) },
                modifier = contentModifier,
            )
        }
    }
}

@Composable
private fun ArticleDetailContent(
    article: ArticleDetailViewData,
    onReadFullArticleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        if (article.imageUrl != null) {
            ArticleImage(imageUrl = article.imageUrl)
        }
        Column(modifier = Modifier.padding(NewsAppTheme.spacing.default)) {
            Pill(text = article.sourceName)
            Spacer(modifier = Modifier.height(NewsAppTheme.spacing.small))
            Text(text = article.title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(NewsAppTheme.spacing.small))
            if (article.author != null) {
                Text(
                    text = stringResource(R.string.article_detail_author, article.author),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Text(
                text = article.publishedAt,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (article.description != null) {
                Spacer(modifier = Modifier.height(NewsAppTheme.spacing.medium))
                Text(text = article.description, style = MaterialTheme.typography.bodyLarge)
            }
            if (article.content != null) {
                Spacer(modifier = Modifier.height(NewsAppTheme.spacing.default))
                Text(
                    text = article.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(NewsAppTheme.spacing.medium))
            PrimaryButton(
                text = stringResource(R.string.article_detail_read_full_article),
                onClick = onReadFullArticleClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ArticleDetailScreenPreview(
    @PreviewParameter(ArticleDetailScreenPreviewParameterProvider::class)
    uiState: ArticleDetailUiState,
) {
    PreviewContainer(uiState, contentPadding = 0.dp) {
        ArticleDetailScreen(uiState = it, onBackClick = {}, onReadFullArticleClick = {})
    }
}
