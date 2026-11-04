package com.dejevij.newsapp.di

import com.dejevij.newsapp.data.network.ApiKeyInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
object LoggingInterceptorModule {

    @Provides
    @IntoSet
    @DebugInterceptors
    fun provideLoggingInterceptor(): Interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        redactHeader(ApiKeyInterceptor.HEADER)
    }
}
