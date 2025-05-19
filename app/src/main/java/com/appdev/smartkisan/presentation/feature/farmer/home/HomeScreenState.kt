package com.appdev.smartkisan.presentation.feature.farmer.home

import com.appdev.smartkisan.domain.model.WeatherUiModel


enum class LocationPermissionState {
    UNKNOWN,        // Initial state
    GRANTED,        // Permission granted
    PERMANENTLY_DENIED, // Permission denied and "Don't ask again" selected
    LOCATION_DISABLED // Permission granted but location is turned off
}

data class HomeScreenState(
    val isLoading: Boolean = false,
    val weatherData: com.appdev.smartkisan.domain.model.WeatherUiModel? = null,
    val userImage:String="",
    val userName:String="",
    val locationPermissionState: LocationPermissionState = LocationPermissionState.UNKNOWN,
    val showLocationDialog: Boolean = false,
    val statusMessage: String = "Tap button below to get weather information"
)