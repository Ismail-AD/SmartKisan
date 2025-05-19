package com.appdev.smartkisan.presentation.feature.farmer.home

interface HomeScreenActions {
    data class FetchWeatherData(val lat: Double, val lon: Double) : HomeScreenActions
    data class SetLocation(val location: String) : HomeScreenActions
    data class UpdateLocationPermission(val state: LocationPermissionState) : HomeScreenActions
    object GoToChatBotScreen : HomeScreenActions
    object CheckUserData : HomeScreenActions
    object GoToNewsScreen : HomeScreenActions
    object ShowLocationDialog : HomeScreenActions
    object DismissLocationDialog : HomeScreenActions
    object RequestCurrentLocation : HomeScreenActions
    object RequestWeatherUpdate : HomeScreenActions
    object GoToMapScreen : HomeScreenActions
}