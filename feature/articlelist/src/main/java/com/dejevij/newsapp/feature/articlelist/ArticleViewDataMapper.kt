package com.dejevij.newsapp.feature.articlelist

import com.dejevij.newsapp.core.model.Article
import com.dejevij.newsapp.core.ui.RelativeTimeFormatter
import javax.inject.Inject

class ArticleViewDataMapper @Inject constructor(
    private val relativeTimeFormatter: RelativeTimeFormatter,
) {

    fun map(article: Article) = ArticleViewData(
        url = article.url,
        title = article.title,
        sourceName = article.sourceName,
        description = article.description,
        imageUrl = article.imageUrl,
        publishedAt = relativeTimeFormatter.format(article.publishedAt),
    )
}
