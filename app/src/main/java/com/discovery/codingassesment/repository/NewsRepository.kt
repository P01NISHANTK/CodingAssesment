package com.discovery.codingassesment.repository

import android.util.Log
import com.discovery.codingassesment.data.model.NewsHeadlinesData
import com.discovery.codingassesment.net.Api
import com.discovery.codingassesment.net.NewsAPIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private var apiService: Api
)  {

    companion object {
        val TAG: String = NewsRepository::class.java.simpleName
    }

    suspend fun fetchNewsHeadlines(country: String?, apiKey: String?): Flow<NewsAPIState<NewsHeadlinesData>> {

        return flow {
            val data = apiService.getNewsHeadlines(country!!, apiKey!!)
            Log.d(TAG, "total news size: ${data.totalResults}")
            emit(NewsAPIState.success(data))
        }.flowOn(Dispatchers.IO)

    }
}