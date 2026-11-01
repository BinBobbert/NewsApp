package com.dejevij.newsapp.ui.articlelist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.dejevij.newsapp.data.DataError
import com.dejevij.newsapp.model.Category

internal class ArticleListScreenPreviewParameterProvider :
    PreviewParameterProvider<ArticleListUiState> {
    private val country = previewCountry("us", "United States", "🇺🇸")
    private val articles = ArticleListContent.Articles(
        listOf(
            previewArticle(url = "1"),
            previewArticle(url = "2", imageUrl = null),
            previewArticle(url = "3", description = null),
        ),
    )

    override val values = sequenceOf(
        ArticleListUiState(content = articles, country = country),
        ArticleListUiState(
            content = articles,
            country = country,
            selectedCategories = setOf(Category.SPORTS, Category.TECHNOLOGY),
        ),
        ArticleListUiState(content = ArticleListContent.Loading, country = country),
        ArticleListUiState(content = ArticleListContent.Empty, country = country),
        ArticleListUiState(
            content = ArticleListContent.Empty,
            country = country,
            selectedCategories = setOf(Category.SCIENCE),
        ),
        ArticleListUiState(
            content = ArticleListContent.Error(DataError.NoConnection),
            country = country,
        ),
    )
}
