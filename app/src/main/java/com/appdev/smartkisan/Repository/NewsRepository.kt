package com.appdev.smartkisan.Repository


import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.data.NewsResponse
import com.appdev.smartkisan.retrofit.NewsApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsApiService: NewsApiService
) {
    suspend fun getAgricultureNews(): Flow<ResultState<NewsResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val response = newsApiService.getNews()
            if (response.isSuccessful && response.body() != null) {
                emit(ResultState.Success(response.body()!!))
            } else {
                emit(ResultState.Failure(Throwable("Failed to fetch news: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(ResultState.Failure(e))
        }
    }

}