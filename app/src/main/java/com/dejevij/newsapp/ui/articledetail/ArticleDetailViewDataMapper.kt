package com.dejevij.newsapp.ui.articledetail

import com.dejevij.newsapp.model.Article
import com.dejevij.newsapp.ui.RelativeTimeFormatter
import javax.inject.Inject

class ArticleDetailViewDataMapper @Inject constructor(
    private val relativeTimeFormatter: RelativeTimeFormatter,
) {

    fun map(article: Article) = ArticleDetailViewData(
        url = article.url,
        title = article.title,
        sourceName = article.sourceName,
        author = article.author,
        publishedAt = relativeTimeFormatter.format(article.publishedAt),
        description = article.description,
        content = article.content,
        imageUrl = article.imageUrl,
    )
}
