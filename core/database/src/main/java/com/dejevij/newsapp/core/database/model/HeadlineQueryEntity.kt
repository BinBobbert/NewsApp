package com.dejevij.newsapp.core.database.model

import androidx.room.Entity

@Entity(tableName = "headline_queries", primaryKeys = ["country", "category"])
data class HeadlineQueryEntity(
    val country: String,
    val category: String,
    val fetchedAt: Long,
)
