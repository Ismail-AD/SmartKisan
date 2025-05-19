package com.appdev.smartkisan.presentation.feature.farmer.account


import android.net.Uri
import com.appdev.smartkisan.domain.model.UserEntity

data class UserProfileState(
    val userEntity: UserEntity = UserEntity(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,

    // Dialog visibility states
    val showEditUsernameDialog: Boolean = false,
    val showUpdatePasswordDialog: Boolean = false,
    val showImageConfirmationDialog: Boolean = false,
    val showLogoutConfirmDialog: Boolean = false,

    // Temporary fields for dialogs
    val temporaryUsername: String = "",
    val temporaryCurrentPassword: String = "",
    val temporaryPassword: String = "",
    val temporaryConfirmPassword: String = "",

    // Selected image URI for profile picture update
    val selectedImageUri: Uri? = null,

    // Field validation errors
    val usernameError: String? = null,
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,

    // Logout confirmation
    val isLogoutConfirmed: Boolean = false
)