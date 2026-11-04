package com.dejevij.newsapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dejevij.newsapp.core.database.dao.HeadlineDao
import com.dejevij.newsapp.core.database.model.ArticleEntity
import com.dejevij.newsapp.core.database.model.HeadlineEntity
import com.dejevij.newsapp.core.database.model.HeadlineQueryEntity

@Database(
    entities = [ArticleEntity::class, HeadlineEntity::class, HeadlineQueryEntity::class],
    version = 1,
)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun headlineDao(): HeadlineDao
}
