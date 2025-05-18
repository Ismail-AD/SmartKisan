package com.appdev.smartkisan.ViewModel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Actions.HomeScreenActions
import com.appdev.smartkisan.Repository.WeatherRepository
import com.appdev.smartkisan.States.LocationPermissionState
import com.appdev.smartkisan.States.WeatherState
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.Utils.SessionManagement
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.*

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    val weatherRepository: WeatherRepository, val geocoder: Geocoder,
    private val fusedLocationClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context,
    val sessionManagement: SessionManagement
) : ViewModel() {

    private val _weatherState = MutableStateFlow(WeatherState())
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()
    val TAG = "HAZQ"

    init {
        checkPermissionsAndLocationStatus()
        _weatherState.value = _weatherState.value.copy(userName = getUserName(), userImage = getProfileImage())
    }

    fun getUserName():String{
        return sessionManagement.getUserName() ?:""
    }
    fun getProfileImage():String{
        return sessionManagement.getUserImage() ?:""
    }

    private fun checkPermissionsAndLocationStatus() {
        // Check location permission
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasLocationPermission) {
            updatePermissionState(LocationPermissionState.PERMANENTLY_DENIED)
            return
        }

        // Permission granted, check if location is enabled
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = LocationManagerCompat.isLocationEnabled(locationManager)

        if (!isLocationEnabled) {
            updatePermissionState(LocationPermissionState.LOCATION_DISABLED)
            return
        }

        // All good, get weather data
        updatePermissionState(LocationPermissionState.GRANTED)
        getCurrentLocation()
    }


    private fun updatePermissionState(state: LocationPermissionState) {
        val statusMessage = when (state) {
            LocationPermissionState.PERMANENTLY_DENIED ->
                "Location permission denied. Please enable location in settings."

            LocationPermissionState.LOCATION_DISABLED ->
                "Location is turned off. Please enable device location."

            LocationPermissionState.UNKNOWN ->
                "Location permission required for weather updates."

            LocationPermissionState.GRANTED ->
                "Fetching weather data..."
        }

        _weatherState.update {
            it.copy(
                locationPermissionState = state,
                statusMessage = statusMessage
            )
        }
    }

    fun onAction(action: HomeScreenActions) {
        when (action) {
            is HomeScreenActions.FetchWeatherData -> {
                // First get the location name, then fetch weather data
                fetchLocationAndWeather(action.lat, action.lon)
            }

            is HomeScreenActions.SetLocation -> {
                _weatherState.update {
                    it.copy(
                        weatherData = it.weatherData?.copy(location = action.location)
                    )
                }
            }

            is HomeScreenActions.UpdateLocationPermission -> {
                _weatherState.update {
                    it.copy(locationPermissionState = action.state)
                }

                // Take action based on the new permission state
                when (action.state) {
                    LocationPermissionState.GRANTED -> {
                        getCurrentLocation()
                    }

                    LocationPermissionState.LOCATION_DISABLED -> {
                        _weatherState.update { it.copy(showLocationDialog = true) }
                    }

                    LocationPermissionState.PERMANENTLY_DENIED -> {
                        _weatherState.update { it.copy(showLocationDialog = true) }
                    }

                    else -> {} // No action needed for UNKNOWN state
                }
            }

            is HomeScreenActions.ShowLocationDialog -> {
                _weatherState.update { it.copy(showLocationDialog = true) }
            }

            is HomeScreenActions.DismissLocationDialog -> {
                _weatherState.update { it.copy(showLocationDialog = false) }
            }

            is HomeScreenActions.RequestCurrentLocation -> {
                getCurrentLocation()
            }

            else -> {} // Handle other actions
        }
    }

    // New function to handle fetching location name first, then weather data
    private fun fetchLocationAndWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                // First, get location name
                val locationName = getLocationNameSuspend(lat, lon)

                // Set the location in state immediately
                val updatedLocationName = locationName ?: "Unknown Location"
                _weatherState.update {
                    it.copy(
                        weatherData = it.weatherData?.copy(location = updatedLocationName)
                            ?: createEmptyWeatherModelWithLocation(updatedLocationName)
                    )
                }

                // Then fetch weather data with location already set
                fetchWeatherData(lat, lon)
            } catch (e: Exception) {
                _weatherState.update {
                    it.copy(
                        isLoading = false,
                        statusMessage = "Error: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    // Helper function to create an empty weather model with just location set
    private fun createEmptyWeatherModelWithLocation(location: String): com.appdev.smartkisan.data.WeatherUiModel {
        return com.appdev.smartkisan.data.WeatherUiModel(
            temperature = 0,
            weatherDescription = "",
            location = location,
            iconCode = ""
        )
    }

    // Suspend function version of getLocationName
    private suspend fun getLocationNameSuspend(lat: Double, lon: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(lat, lon, 1)

                if (addresses.isNullOrEmpty()) {
                    return@withContext null
                }

                val address = addresses[0]
                val cityName = address.locality ?: address.subAdminArea ?: "Unknown"
                val countryName = address.countryName ?: ""

                val locationString = "$cityName, $countryName"
                locationString
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun fetchWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch {
            weatherRepository.getCurrentWeather(lat, lon).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _weatherState.update { it.copy(isLoading = true) }
                    }

                    is ResultState.Success -> {
                        val weatherResponse = result.data
                        val currentWeatherData = _weatherState.value.weatherData

                        val location = currentWeatherData?.location ?: "Unknown Location"
                        val weatherUiModel = weatherRepository.mapToUiModel(
                            weatherResponse,
                            location
                        )
                        Log.d("ZAW","${weatherUiModel}")

                        _weatherState.update {
                            it.copy(
                                isLoading = false,
                                weatherData = weatherUiModel,
                                statusMessage = "" // Clear message when we have weather data
                            )
                        }
                        
                    }

                    is ResultState.Failure -> {
                        val errorMessage = result.msg.message ?: "Unknown error occurred"
                        _weatherState.update {
                            it.copy(
                                isLoading = false,
                                statusMessage = "Error: $errorMessage"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getLocationName(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val addresses = geocoder.getFromLocation(lat, lon, 1)

                if (addresses.isNullOrEmpty()) {
                    return@launch
                }

                val address = addresses[0]
                val cityName = address.locality ?: address.subAdminArea ?: "Unknown"
                val countryName = address.countryName ?: ""

                val locationString = "$cityName, $countryName"

                onAction(HomeScreenActions.SetLocation(locationString))
            } catch (e: Exception) {
                // Handle exception silently
            }
        }
    }

    private fun getCurrentLocation() {
        try {
            // Check if permission is granted (should be at this point, but double-check)
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                _weatherState.update {
                    it.copy(
                        isLoading = false,
                        statusMessage = "Location permission required"
                    )
                }
                return
            }

            // Using the new LocationRequest.Builder approach (non-deprecated)
            val locationRequest =
                com.google.android.gms.location.LocationRequest.Builder(5000) // 5 second interval
                    .setPriority(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY)
                    .setMinUpdateIntervalMillis(2000) // 2 seconds for fastest interval
                    .setMaxUpdates(5) // Only need one update
                    .build()

            val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    // Get the most recent location
                    locationResult.locations.lastOrNull()?.let { location ->
                        // Successfully got location, remove the callback to stop updates
                        fusedLocationClient.removeLocationUpdates(this)

                        // Fetch weather with the new location
                        onAction(
                            HomeScreenActions.FetchWeatherData(
                                location.latitude,
                                location.longitude
                            )
                        )
                    } ?: run {
                        _weatherState.update {
                            it.copy(
                                isLoading = false,
                                statusMessage = "Couldn't retrieve location. Please try again."
                            )
                        }
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            }

            // First try getting the last known location (which might be null)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    // If we have a recent location, use it immediately
                    onAction(
                        HomeScreenActions.FetchWeatherData(
                            location.latitude,
                            location.longitude
                        )
                    )
                } else {
                    // Otherwise, actively request location updates
                    _weatherState.update {
                        it.copy(
                            isLoading = true,
                            statusMessage = "Getting your location..."
                        )
                    }

                    // Request location updates using the current API
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        null // Looper
                    )
                }
            }.addOnFailureListener { e ->
                // Try requesting location updates directly
                _weatherState.update {
                    it.copy(
                        isLoading = true,
                        statusMessage = "Getting your location..."
                    )
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null // Looper
                )
            }
        } catch (e: SecurityException) {
            _weatherState.update {
                it.copy(
                    isLoading = false,
                    statusMessage = "Security error: ${e.localizedMessage}"
                )
            }
        } catch (e: Exception) {
            _weatherState.update {
                it.copy(
                    isLoading = false,
                    statusMessage = "Error: ${e.localizedMessage}"
                )
            }
        }
    }
}