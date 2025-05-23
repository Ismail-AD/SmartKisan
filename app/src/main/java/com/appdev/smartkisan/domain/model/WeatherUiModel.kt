package com.appdev.smartkisan.domain.model

data class WeatherUiModel(
    val temperature: Int, // Temperature in Celsius or Fahrenheit based on user preference
    val weatherDescription: String,
    val location: String,
    val iconCode: String
)