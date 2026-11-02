package com.dejevij.newsapp.ui.articlelist

import android.text.format.DateUtils
import com.dejevij.newsapp.model.Article
import javax.inject.Inject
import kotlin.time.Clock

class ArticleViewDataMapper @Inject constructor(private val clock: Clock) {

    fun map(article: Article) = ArticleViewData(
        url = article.url,
        title = article.title,
        sourceName = article.sourceName,
        description = article.description,
        imageUrl = article.imageUrl,
        publishedAt = DateUtils.getRelativeTimeSpanString(
            article.publishedAt.toEpochMilliseconds(),
            clock.now().toEpochMilliseconds(),
            DateUtils.MINUTE_IN_MILLIS,
        ).toString(),
    )
}
