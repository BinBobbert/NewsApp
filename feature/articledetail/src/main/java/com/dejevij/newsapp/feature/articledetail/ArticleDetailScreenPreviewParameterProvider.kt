package com.dejevij.newsapp.feature.articledetail

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

internal class ArticleDetailScreenPreviewParameterProvider :
    PreviewParameterProvider<ArticleDetailUiState> {
    override val values = sequenceOf(
        ArticleDetailUiState.Content(previewArticleDetail()),
        ArticleDetailUiState.Content(
            previewArticleDetail(
                author = null,
                description = null,
                content = null,
                imageUrl = null,
            ),
        ),
        ArticleDetailUiState.Loading,
        ArticleDetailUiState.NotAvailable,
    )
}

internal fun previewArticleDetail(
    url: String = "https://example.com/article",
    title: String = "New chip promises week-long phone battery life",
    author: String? = "Jane Doe",
    description: String? = "Researchers say the design cuts idle power use by 80 percent.",
    content: String? = "The prototype, unveiled on Monday, combines a new low-power core with…",
    imageUrl: String? = "https://example.com/image.jpg",
) = ArticleDetailViewData(
    url = url,
    title = title,
    sourceName = "BBC News",
    author = author,
    publishedAt = "2 hours ago",
    description = description,
    content = content,
    imageUrl = imageUrl,
)
