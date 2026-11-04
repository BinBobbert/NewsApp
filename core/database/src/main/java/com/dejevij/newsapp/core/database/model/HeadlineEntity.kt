package com.dejevij.newsapp.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

// Links an article to a top headlines query; the same article can appear in several queries.
@Entity(
    tableName = "headlines",
    primaryKeys = ["country", "category", "url"],
    foreignKeys = [
        ForeignKey(
            entity = ArticleEntity::class,
            parentColumns = ["url"],
            childColumns = ["url"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("url")],
)
data class HeadlineEntity(
    val country: String,
    val category: String,
    val url: String,
    val position: Int,
)
