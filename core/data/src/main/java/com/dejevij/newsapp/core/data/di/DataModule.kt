package com.dejevij.newsapp.core.data.di

import com.dejevij.newsapp.core.data.ArticleRepository
import com.dejevij.newsapp.core.data.CountryRepository
import com.dejevij.newsapp.core.data.DataStoreCountryRepository
import com.dejevij.newsapp.core.data.DefaultArticleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindArticleRepository(repository: DefaultArticleRepository): ArticleRepository

    @Binds
    fun bindCountryRepository(repository: DataStoreCountryRepository): CountryRepository
}
