package com.appdev.smartkisan.data.repository


import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.data.remote.retrofit.WeatherApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.math.roundToInt

class WeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService
) {
    suspend fun getCurrentWeather(lat: Double, lon: Double): Flow<ResultState<com.appdev.smartkisan.domain.model.Weather>> = flow {
        emit(ResultState.Loading)
        try {
            val response = weatherApiService.getWeatherData(lat, lon)
            if (response.isSuccessful && response.body() != null) {
                emit(ResultState.Success(response.body()!!))
            } else {
                emit(ResultState.Failure(Throwable("Failed to fetch weather data: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(ResultState.Failure(e))
        }
    }

    fun mapToUiModel(weatherResponse: com.appdev.smartkisan.domain.model.Weather, location: String): com.appdev.smartkisan.domain.model.WeatherUiModel {
        val tempCelsius = (weatherResponse.main.temp - 273.15).roundToInt()
        return com.appdev.smartkisan.domain.model.WeatherUiModel(
            temperature = tempCelsius,
            weatherDescription = weatherResponse.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercaseChar() }
                ?: "Unknown",
            location = location,
            iconCode = weatherResponse.weather.firstOrNull()?.icon ?: "01d"
        )
    }

}

// Extension function to capitalize the first letter of a string
fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}