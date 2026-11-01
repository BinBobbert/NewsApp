package com.dejevij.newsapp.ui.articlelist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.dejevij.newsapp.data.DataError
import com.dejevij.newsapp.model.Category
import com.dejevij.newsapp.model.Country

internal class ArticleListScreenPreviewParameterProvider :
    PreviewParameterProvider<ArticleListUiState> {
    private val articles = ArticleListContent.Articles(
        listOf(
            previewArticle(url = "1"),
            previewArticle(url = "2", imageUrl = null),
            previewArticle(url = "3", description = null)
        )
    )

    override val values = sequenceOf(
        ArticleListUiState(content = articles, country = Country.Default),
        ArticleListUiState(
            content = articles,
            country = Country.Default,
            selectedCategories = setOf(Category.SPORTS, Category.TECHNOLOGY)
        ),
        ArticleListUiState(content = ArticleListContent.Loading, country = Country.Default),
        ArticleListUiState(content = ArticleListContent.Empty, country = Country.Default),
        ArticleListUiState(
            content = ArticleListContent.Error(DataError.NoConnection),
            country = Country.Default
        )
    )
}
