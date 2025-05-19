package com.appdev.smartkisan.data.remote.retrofit


import com.appdev.smartkisan.BuildConfig
import com.appdev.smartkisan.domain.model.NewsResponse
import com.appdev.smartkisan.domain.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("search-news")
    suspend fun getNews(
        @Query("text") text: String = "agriculture",
        @Query("language") language: String = "en",
        @Query("earliest-publish-date") earliestPublishDate: String,
        @Query("latest-publish-date") latestPublishDate: String,
        @Query("source-country") sourceCountry: String = "pk",
        @Query("offset") offset: Int = 0, // Added offset parameter for pagination
        @Query("number") number: Int = 10, // Default page size
        @Query("api-key") apiKey: String = BuildConfig.NEWS_KEY
    ): Response<NewsResponse>
}