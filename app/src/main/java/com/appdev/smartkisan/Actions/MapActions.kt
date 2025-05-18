package com.appdev.smartkisan.Actions

import android.location.Location
import com.appdev.smartkisan.data.SellerMetaData
import com.appdev.smartkisan.data.UserEntity

sealed interface MapActions {
    object LoadSellersData : MapActions
    data class LoadSellersSuccess(val sellers: List<SellerMetaData>) : MapActions
    data class LoadSellersError(val message: String) : MapActions

    data class SelectSeller(val seller: SellerMetaData) : MapActions
    data class LoadSellerInfo(val sellerId: String) : MapActions
    data class LoadSellerInfoSuccess(val userInfo: UserEntity) : MapActions
    data class LoadSellerInfoError(val message: String) : MapActions
    data class UpdateUserLocation(val location: Location) : MapActions
    object ShowSellerDetailsDialog : MapActions
    object DismissSellerDetailsDialog : MapActions

}