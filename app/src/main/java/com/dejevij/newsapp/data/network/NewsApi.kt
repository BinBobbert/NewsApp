package com.dejevij.newsapp.data.network

import com.dejevij.newsapp.data.network.model.TopHeadlinesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String,
        @Query("pageSize") pageSize: Int
    ): TopHeadlinesResponse
}
