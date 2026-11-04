package com.dejevij.newsapp.core.network

import java.util.concurrent.TimeUnit
import javax.inject.Inject
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

// NewsAPI marks every response `no-cache`. Overriding that for successful responses lets OkHttp
// answer repeat requests from its disk cache, so they don't count against the daily quota.
class ResponseCacheInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (!response.isSuccessful) return response
        return response.newBuilder()
            .header(HEADER_CACHE_CONTROL, MAX_AGE.toString())
            .removeHeader(HEADER_PRAGMA)
            .removeHeader(HEADER_EXPIRES)
            .build()
    }

    private companion object {

        const val HEADER_CACHE_CONTROL = "Cache-Control"
        const val HEADER_PRAGMA = "Pragma"
        const val HEADER_EXPIRES = "Expires"
        const val MAX_AGE_MINUTES = 30
        val MAX_AGE = CacheControl.Builder().maxAge(MAX_AGE_MINUTES, TimeUnit.MINUTES).build()
    }
}
