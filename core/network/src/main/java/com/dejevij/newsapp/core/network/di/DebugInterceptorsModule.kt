package com.dejevij.newsapp.core.network.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import okhttp3.Interceptor

// Declares the set as possibly empty, so release builds compile without the debug source set.
@Module
@InstallIn(SingletonComponent::class)
interface DebugInterceptorsModule {

    @Multibinds
    @DebugInterceptors
    fun debugInterceptors(): Set<Interceptor>
}
