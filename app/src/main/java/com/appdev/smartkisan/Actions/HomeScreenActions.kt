package com.appdev.smartkisan.Actions

import com.appdev.smartkisan.States.LocationPermissionState

interface HomeScreenActions {
    data class FetchWeatherData(val lat: Double, val lon: Double) : HomeScreenActions
    data class SetLocation(val location: String) : HomeScreenActions
    data class UpdateLocationPermission(val state: LocationPermissionState) : HomeScreenActions
    object GoToChatBotScreen : HomeScreenActions
    object GoToNewsScreen : HomeScreenActions
    object ShowLocationDialog : HomeScreenActions
    object DismissLocationDialog : HomeScreenActions
    object RequestCurrentLocation : HomeScreenActions
    object RequestWeatherUpdate : HomeScreenActions
}