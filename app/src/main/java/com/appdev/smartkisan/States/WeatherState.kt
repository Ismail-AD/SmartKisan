package com.appdev.smartkisan.States

import com.appdev.smartkisan.data.WeatherUiModel


enum class LocationPermissionState {
    UNKNOWN,        // Initial state
    GRANTED,        // Permission granted
    PERMANENTLY_DENIED, // Permission denied and "Don't ask again" selected
    LOCATION_DISABLED // Permission granted but location is turned off
}

data class WeatherState(
    val isLoading: Boolean = false,
    val weatherData: WeatherUiModel? = null,
    val locationPermissionState: LocationPermissionState = LocationPermissionState.UNKNOWN,
    val showLocationDialog: Boolean = false,
    val statusMessage: String = "Tap button below to get weather information"
)