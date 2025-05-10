package com.appdev.smartkisan.Actions

import android.net.Uri
import com.appdev.smartkisan.States.LocationPermissionState

sealed interface SellerProfileActions {
    object LoadSellerProfile : SellerProfileActions
    object ShowEditUsernameDialog : SellerProfileActions
    object ShowEditShopNameDialog : SellerProfileActions
    object ShowUpdatePasswordDialog : SellerProfileActions
    object ShowLocationPicker : SellerProfileActions
    object DismissAllDialogs : SellerProfileActions
    object ClearError : SellerProfileActions
    object DismissLocationDialog : SellerProfileActions
    object Logout : SellerProfileActions
    object HideLogoutConfirmDialog : SellerProfileActions
    object ShowLogoutConfirmDialog : SellerProfileActions

    object LaunchImagePicker : SellerProfileActions
    data class ShowImageConfirmationDialog(val uri: Uri) : SellerProfileActions
    object DismissImageConfirmationDialog : SellerProfileActions
    object LaunchLocationSettings : SellerProfileActions
    object DismissMessage : SellerProfileActions

    data class SetTemporaryContact(val contact: String) : SellerProfileActions
    data class UpdateContact(val contact: String) : SellerProfileActions
    object ShowEditContactDialog : SellerProfileActions
    data class UpdateUsername(val username: String) : SellerProfileActions
    data class UpdateShopName(val shopName: String) : SellerProfileActions
    data class SetTemporaryCurrentPassword(val password: String) : SellerProfileActions
    data class UpdatePassword(val currentPassword:String,val password: String, val confirmPassword: String) : SellerProfileActions
    data class UpdateShopLocation(val lat:Double,val long:Double) : SellerProfileActions
    data class UpdateLocationPermission(val state: LocationPermissionState) : SellerProfileActions
    data class UpdateProfileImage(val uri: Uri) : SellerProfileActions

    data class SetTemporaryUsername(val username: String) : SellerProfileActions
    data class SetTemporaryShopName(val shopName: String) : SellerProfileActions
    data class SetTemporaryPassword(val password: String) : SellerProfileActions
    data class SetTemporaryConfirmPassword(val password: String) : SellerProfileActions
}

