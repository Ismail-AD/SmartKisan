package com.appdev.smartkisan.ViewModel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Actions.MapActions
import com.appdev.smartkisan.Repository.Repository
import com.appdev.smartkisan.States.MapState
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.data.SellerMetaData
import com.appdev.smartkisan.data.UserEntity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: Repository,
    private val fusedLocationClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context,
    private val geocoder: Geocoder
) : ViewModel() {

    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state.asStateFlow()

    fun onAction(action: MapActions) {
        when (action) {
            is MapActions.LoadSellersData -> loadSellersData()
            is MapActions.LoadSellersSuccess -> {
                _state.value = _state.value.copy(
                    isLoading = false,
                    sellers = action.sellers,
                    errorMessage = null
                )
            }
            is MapActions.LoadSellersError -> {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = action.message
                )
            }
            is MapActions.UpdateUserLocation -> {
                _state.value = _state.value.copy(
                    userLocation = action.location
                )
            }
            is MapActions.SelectSeller -> {
                _state.value = _state.value.copy(
                    selectedSeller = action.seller,
                    isSellerInfoLoading = true
                )
                loadSellerInfo(action.seller.id)
                _state.value = _state.value.copy(
                    showSellerDetailsDialog = true
                )
            }
            is MapActions.LoadSellerInfoSuccess -> {
                _state.value = _state.value.copy(
                    selectedSellerInfo = action.userInfo,
                    isSellerInfoLoading = false
                )
            }
            is MapActions.LoadSellerInfoError -> {
                _state.value = _state.value.copy(
                    isSellerInfoLoading = false,
                    errorMessage = action.message
                )
            }
            is MapActions.DismissSellerDetailsDialog -> {
                _state.value = _state.value.copy(
                    showSellerDetailsDialog = false,
                    selectedSeller = null,
                    selectedSellerInfo = null
                )
            }
            else -> {}
        }
    }

    private fun loadSellersData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            repository.getAllSellersWithLocations().collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {
                        onAction(MapActions.LoadSellersSuccess(result.data))
                    }
                    is ResultState.Failure -> {
                        onAction(MapActions.LoadSellersError(result.msg.message ?: "Failed to load sellers"))
                    }
                    is ResultState.Loading -> {
                        // Already handling loading state when function is called
                    }
                }
            }
        }
    }

    private fun loadSellerInfo(sellerId: String) {
        viewModelScope.launch {
            repository.fetchUserById(sellerId).collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {
                        onAction(MapActions.LoadSellerInfoSuccess(result.data))
                    }
                    is ResultState.Failure -> {
                        onAction(MapActions.LoadSellerInfoError(result.msg.message ?: "Failed to load seller info"))
                    }
                    is ResultState.Loading -> {
                        // Loading state is handled when SelectSeller action is processed
                    }
                }
            }
        }
    }

    private fun getCurrentLocation() {
        try {
            // Check if permission is granted
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                _state.value = _state.value.copy(
                    errorMessage = "Location permission required"
                )
                return
            }

            // Create location request
            val locationRequest = LocationRequest.Builder(5000) // 5 second interval
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(2000) // 2 seconds for fastest interval
                .setMaxUpdates(5) // Only need a few updates
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    // Get the most recent location
                    locationResult.locations.lastOrNull()?.let { location ->
                        // Successfully got location, remove the callback to stop updates
                        fusedLocationClient.removeLocationUpdates(this)
                        onAction(MapActions.UpdateUserLocation(location))
                    } ?: run {
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            }

            // First try getting the last known location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    // If we have a recent location, use it immediately
                    onAction(MapActions.UpdateUserLocation(location))
                } else {
                    // Otherwise, actively request location updates
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        null // Looper
                    )
                }
            }.addOnFailureListener {
                // Try requesting location updates directly
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null // Looper
                )
            }
        } catch (e: SecurityException) {
            _state.value = _state.value.copy(
                errorMessage = "Security error: ${e.localizedMessage}"
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                errorMessage = "Error: ${e.localizedMessage}"
            )
        }
    }

    init {
        loadSellersData()
        getCurrentLocation()
    }
}