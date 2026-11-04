package com.dejevij.newsapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.dejevij.newsapp.core.database.model.ArticleEntity
import com.dejevij.newsapp.core.database.model.HeadlineEntity
import com.dejevij.newsapp.core.database.model.HeadlineQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HeadlineDao {

    // An article in several of the categories is returned once. Articles published at the same
    // time keep the order the API returned them in.
    @Query(
        """
        SELECT articles.* FROM articles
        INNER JOIN headlines ON headlines.url = articles.url
        WHERE headlines.country = :country AND headlines.category IN (:categories)
        GROUP BY articles.url
        ORDER BY articles.publishedAt DESC, MIN(headlines.position)
        """,
    )
    fun observeHeadlines(country: String, categories: Collection<String>): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE url = :url")
    fun observeArticle(url: String): Flow<ArticleEntity?>

    @Query("SELECT * FROM headline_queries WHERE country = :country AND category IN (:categories)")
    suspend fun getQueries(
        country: String,
        categories: Collection<String>,
    ): List<HeadlineQueryEntity>

    // Replaces the headlines of each fetched category in one transaction, so observers never see
    // a partially written refresh, and drops articles no query refers to anymore.
    @Transaction
    suspend fun replaceHeadlines(
        country: String,
        headlines: Map<String, List<ArticleEntity>>,
        fetchedAt: Long,
    ) {
        upsertArticles(headlines.values.flatten().distinctBy { it.url })
        headlines.forEach { (category, articles) ->
            deleteHeadlines(country, category)
            insertHeadlines(
                articles.mapIndexed { position, article ->
                    HeadlineEntity(country, category, article.url, position)
                },
            )
            upsertQuery(HeadlineQueryEntity(country, category, fetchedAt))
        }
        deleteOrphanedArticles()
    }

    @Upsert
    suspend fun upsertArticles(articles: List<ArticleEntity>)

    @Insert
    suspend fun insertHeadlines(headlines: List<HeadlineEntity>)

    @Upsert
    suspend fun upsertQuery(query: HeadlineQueryEntity)

    @Query("DELETE FROM headlines WHERE country = :country AND category = :category")
    suspend fun deleteHeadlines(country: String, category: String)

    @Query("DELETE FROM articles WHERE url NOT IN (SELECT url FROM headlines)")
    suspend fun deleteOrphanedArticles()
}
