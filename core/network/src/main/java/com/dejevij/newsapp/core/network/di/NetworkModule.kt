package com.dejevij.newsapp.core.network.di

import com.dejevij.newsapp.core.network.ApiKeyInterceptor
import com.dejevij.newsapp.core.network.BuildConfig
import com.dejevij.newsapp.core.network.NewsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiKey

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl

// Only for NewsAPI requests: it adds the API key header, so it must never be used for other hosts.
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NewsApiClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DebugInterceptors

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @ApiKey
    fun provideApiKey(): String = BuildConfig.NEWS_API_KEY

    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = BuildConfig.NEWS_API_BASE_URL

    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    @NewsApiClient
    fun provideNewsApiOkHttpClient(
        okHttpClient: OkHttpClient,
        apiKeyInterceptor: ApiKeyInterceptor,
        @DebugInterceptors debugInterceptors: Set<@JvmSuppressWildcards Interceptor>,
    ): OkHttpClient = okHttpClient.newBuilder()
        .addInterceptor(apiKeyInterceptor)
        .apply { debugInterceptors.forEach(::addInterceptor) }
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        @NewsApiClient okHttpClient: OkHttpClient,
        json: Json,
        @BaseUrl baseUrl: String,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): NewsApi = retrofit.create()
}
