package com.appdev.smartkisan.presentation.feature.Seller.account

import android.net.Uri
import com.appdev.smartkisan.domain.model.UserEntity
import com.appdev.smartkisan.presentation.feature.farmer.home.LocationPermissionState

data class SellerProfileState(
    // Logout confirmation
    val showLogoutConfirmDialog: Boolean = false,
    val isLogoutConfirmed: Boolean = false,
    val seller: com.appdev.smartkisan.domain.model.SellerMetaData = com.appdev.smartkisan.domain.model.SellerMetaData(),
    val userEntity: UserEntity = UserEntity(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showEditUsernameDialog: Boolean = false,
    val showEditShopNameDialog: Boolean = false,
    val showUpdatePasswordDialog: Boolean = false,
    val showLocationPickerMap: Boolean = false,
    val locationPermissionState: LocationPermissionState = LocationPermissionState.UNKNOWN,
    val showLocationDialog: Boolean = false,
    val selectedImageUri: Uri? = null,
    val temporaryUsername: String = "",
    val temporaryShopName: String = "",
    val temporaryCurrentPassword: String = "",  // Add this line
    val temporaryPassword: String = "",
    val temporaryConfirmPassword: String = "",
    val successMessage: String? = null,
    val showEditContactDialog: Boolean = false,
    val temporaryContact: String = "",
    val usernameError: String? = null,       // New field-specific error
    val shopNameError: String? = null,        // New field-specific error
    val contactError: String? = null,         // New field-specific error
    val currentPasswordError: String? = null, // New field-specific error
    val newPasswordError: String? = null,     // New field-specific error
    val confirmPasswordError: String? = null, // New field-specific error
    val showImageConfirmationDialog: Boolean = false, // New field for image confirmation dialog
)