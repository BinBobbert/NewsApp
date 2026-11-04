package com.dejevij.newsapp.testing

import com.dejevij.newsapp.core.data.ArticleRepository
import com.dejevij.newsapp.core.data.CountryRepository
import com.dejevij.newsapp.core.data.di.DataModule
import com.dejevij.newsapp.core.testing.FakeArticleRepository
import com.dejevij.newsapp.core.testing.FakeCountryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DataModule::class])
object FakeDataModule {

    @Provides
    @Singleton
    fun provideArticleRepository(): ArticleRepository =
        FakeArticleRepository().apply { nextArticles = listOf(testArticle) }

    @Provides
    @Singleton
    fun provideCountryRepository(): CountryRepository = FakeCountryRepository()
}
