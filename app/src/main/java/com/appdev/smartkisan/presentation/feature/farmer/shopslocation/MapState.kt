package com.appdev.smartkisan.presentation.feature.farmer.shopslocation

import android.location.Location
import com.appdev.smartkisan.domain.model.UserEntity

data class MapState(
    val isLoading: Boolean = false,
    val sellers: List<com.appdev.smartkisan.domain.model.SellerMetaData> = emptyList(),
    val errorMessage: String? = null,
    val selectedSeller: com.appdev.smartkisan.domain.model.SellerMetaData? = null,
    val isSellerInfoLoading: Boolean = false,
    val selectedSellerInfo: UserEntity? = null,
    val showSellerDetailsDialog: Boolean = false,
    val userLocation: Location? = null,

    )