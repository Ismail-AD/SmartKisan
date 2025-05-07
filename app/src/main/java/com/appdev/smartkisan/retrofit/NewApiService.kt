package com.appdev.smartkisan.retrofit


import com.appdev.smartkisan.BuildConfig
import com.appdev.smartkisan.data.NewsResponse
import com.appdev.smartkisan.data.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("search-news")
    suspend fun getNews(
        @Query("text") text: String = "agriculture",
        @Query("language") language: String = "en",
        @Query("earliest-publish-date") earliestPublishDate: String = "2025-05-05",
        @Query("source-country") sourceCountry: String = "pk",
        @Query("api-key") apiKey: String = BuildConfig.NEWS_KEY
    ): Response<NewsResponse>
}