package com.appdev.smartkisan.Repository

import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.data.NewsResponse
import com.appdev.smartkisan.retrofit.NewsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsApiService: NewsApiService
) {
    fun getAgricultureNews(
        earliestDate: String,
        latestDate: String,
        offset: Int = 0
    ): Flow<ResultState<NewsResponse>> = flow {
        emit(ResultState.Loading)

        try {
            val response = newsApiService.getNews(
                earliestPublishDate = earliestDate,
                latestPublishDate = latestDate,
                offset = offset
            )

            if (response.isSuccessful) {
                response.body()?.let { newsResponse ->
                    emit(ResultState.Success(newsResponse))
                } ?: emit(ResultState.Failure(Exception("Empty response body")))
            } else {
                val errorJson = response.errorBody()?.string()
                val errorMsg = errorJson
                    ?.let { JSONObject(it).optString("message") }
                    ?: "Unknown error"
                emit(ResultState.Failure(Throwable(errorMsg)))
            }
        } catch (e: Exception) {
            emit(ResultState.Failure(e))
        }
    }.flowOn(Dispatchers.IO)
}