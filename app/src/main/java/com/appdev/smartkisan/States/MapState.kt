package com.appdev.smartkisan.States

import android.location.Location
import com.appdev.smartkisan.data.SellerMetaData
import com.appdev.smartkisan.data.UserEntity

data class MapState(
    val isLoading: Boolean = false,
    val sellers: List<SellerMetaData> = emptyList(),
    val errorMessage: String? = null,
    val selectedSeller: SellerMetaData? = null,
    val isSellerInfoLoading: Boolean = false,
    val selectedSellerInfo: UserEntity? = null,
    val showSellerDetailsDialog: Boolean = false,
    val userLocation: Location? = null,

)