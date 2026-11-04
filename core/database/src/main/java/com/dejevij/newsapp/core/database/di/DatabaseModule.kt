package com.dejevij.newsapp.core.database.di

import android.content.Context
import androidx.room.Room
import com.dejevij.newsapp.core.database.NewsDatabase
import com.dejevij.newsapp.core.database.dao.HeadlineDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNewsDatabase(@ApplicationContext context: Context): NewsDatabase =
        Room.databaseBuilder(context, NewsDatabase::class.java, DATABASE_NAME).build()

    @Provides
    fun provideHeadlineDao(database: NewsDatabase): HeadlineDao = database.headlineDao()

    private const val DATABASE_NAME = "news.db"
}
