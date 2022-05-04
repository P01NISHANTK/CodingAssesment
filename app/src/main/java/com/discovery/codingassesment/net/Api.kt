package com.discovery.codingassesment.net

import com.discovery.codingassesment.data.model.NewsHeadlinesData
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface Api {

    @Headers("Content-Type:application/json")
    @GET("/v2/top-headlines")
    suspend fun getNewsHeadlines(
        @Query("country") country: String,
        @Query("apikey") apikey: String
    ) : NewsHeadlinesData
}