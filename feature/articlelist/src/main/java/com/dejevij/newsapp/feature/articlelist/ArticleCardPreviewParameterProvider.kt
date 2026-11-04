package com.dejevij.newsapp.feature.articlelist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

internal class ArticleCardPreviewParameterProvider : PreviewParameterProvider<ArticleViewData> {
    override val values = sequenceOf(
        previewArticle(),
        previewArticle(
            title = "A very long headline that keeps going to show how the card handles " +
                "titles that need more than three lines of text on a phone screen",
            description = null,
            imageUrl = null,
        ),
    )
}

internal fun previewArticle(
    url: String = "https://example.com/article",
    title: String = "New chip promises week-long phone battery life",
    description: String? = "Researchers say the design cuts idle power use by 80 percent.",
    imageUrl: String? = "https://example.com/image.jpg",
) = ArticleViewData(
    url = url,
    title = title,
    sourceName = "BBC News",
    description = description,
    imageUrl = imageUrl,
    publishedAt = "2 hours ago",
)
