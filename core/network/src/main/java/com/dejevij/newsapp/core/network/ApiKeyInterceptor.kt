package com.dejevij.newsapp.core.network

import com.dejevij.newsapp.core.network.di.ApiKey
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor @Inject constructor(@ApiKey private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header(HEADER, apiKey)
            .build()
        return chain.proceed(request)
    }

    companion object {

        const val HEADER = "X-Api-Key"
    }
}
