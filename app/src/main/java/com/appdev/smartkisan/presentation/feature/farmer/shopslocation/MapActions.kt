package com.appdev.smartkisan.presentation.feature.farmer.shopslocation

import android.location.Location
import com.appdev.smartkisan.domain.model.SellerMetaData
import com.appdev.smartkisan.domain.model.UserEntity

sealed interface MapActions {
    object LoadSellersData : MapActions
    data class LoadSellersSuccess(val sellers: List<com.appdev.smartkisan.domain.model.SellerMetaData>) : MapActions
    data class LoadSellersError(val message: String) : MapActions

    data class SelectSeller(val seller: com.appdev.smartkisan.domain.model.SellerMetaData) : MapActions
    data class LoadSellerInfo(val sellerId: String) : MapActions
    data class LoadSellerInfoSuccess(val userInfo: UserEntity) : MapActions
    data class LoadSellerInfoError(val message: String) : MapActions
    data class UpdateUserLocation(val location: Location) : MapActions
    object ShowSellerDetailsDialog : MapActions
    object DismissSellerDetailsDialog : MapActions

}