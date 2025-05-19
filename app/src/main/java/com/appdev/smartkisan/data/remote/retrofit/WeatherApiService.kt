package com.appdev.smartkisan.data.remote.retrofit


import com.appdev.smartkisan.BuildConfig
import com.appdev.smartkisan.domain.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = BuildConfig.WEATHER_KEY
    ): Response<Weather>
}
