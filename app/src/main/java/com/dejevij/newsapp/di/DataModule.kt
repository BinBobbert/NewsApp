package com.dejevij.newsapp.di

import com.dejevij.newsapp.data.ArticleRepository
import com.dejevij.newsapp.data.CountryRepository
import com.dejevij.newsapp.data.DefaultArticleRepository
import com.dejevij.newsapp.data.LocaleCountryRepository
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
    fun bindCountryRepository(repository: LocaleCountryRepository): CountryRepository
}
