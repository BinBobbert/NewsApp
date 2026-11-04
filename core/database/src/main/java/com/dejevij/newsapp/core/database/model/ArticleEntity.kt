package com.dejevij.newsapp.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val url: String,
    val title: String,
    val sourceName: String,
    val author: String?,
    val description: String?,
    val content: String?,
    val imageUrl: String?,
    val publishedAt: Long,
)
