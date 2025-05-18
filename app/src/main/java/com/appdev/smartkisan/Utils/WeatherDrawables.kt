package com.appdev.smartkisan.Utils

import com.appdev.smartkisan.R


/**
 * Maps OpenWeatherMap icon codes to drawable resource IDs
 */
object WeatherDrawables {
    // Map of weather icon codes to drawable resource IDs
    private val weatherDrawableMap = mapOf(
        // Clear sky
        "01d" to R.drawable.day,
        "01n" to R.drawable.day,

        // Few clouds
        "02d" to R.drawable.partlycloudy,
        "02n" to R.drawable.partlycloudy,

        // Scattered clouds
        "03d" to R.drawable.partlycloudy,
        "03n" to R.drawable.partlycloudy,

        // Broken clouds
        "04d" to R.drawable.overcast,
        "04n" to R.drawable.overcast,

        // Shower rain
        "09d" to R.drawable.rain,
        "09n" to R.drawable.rain,

        // Rain
        "10d" to R.drawable.rain,
        "10n" to R.drawable.rain,

        // Thunderstorm
        "11d" to R.drawable.thunderstrom,
        "11n" to R.drawable.thunderstrom,

        // Snow
        "13d" to R.drawable.snow,
        "13n" to R.drawable.snow,

        // Mist
        "50d" to R.drawable.fogg,
        "50n" to R.drawable.fogg
    )

    /**
     * Get the appropriate drawable resource ID for a weather icon code
     * @param iconCode The OpenWeatherMap icon code
     * @return The drawable resource ID
     */
    fun getDrawableForWeather(iconCode: String): Int {
        // Return the mapped drawable or a default one if not found
        return weatherDrawableMap[iconCode] ?: R.drawable.pin
    }
}