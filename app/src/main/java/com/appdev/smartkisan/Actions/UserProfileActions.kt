package com.appdev.smartkisan.Actions


import android.net.Uri

sealed interface UserProfileActions {
    object LoadUserProfile : UserProfileActions

    // Dialog actions
    object ShowEditUsernameDialog : UserProfileActions
    object ShowUpdatePasswordDialog : UserProfileActions
    object DismissAllDialogs : UserProfileActions

    // Username actions
    data class SetTemporaryUsername(val username: String) : UserProfileActions
    data class UpdateUsername(val username: String) : UserProfileActions

    // Password actions
    data class SetTemporaryCurrentPassword(val password: String) : UserProfileActions
    data class SetTemporaryPassword(val password: String) : UserProfileActions
    data class SetTemporaryConfirmPassword(val password: String) : UserProfileActions
    data class UpdatePassword(
        val currentPassword: String,
        val password: String,
        val confirmPassword: String
    ) : UserProfileActions

    // Image actions
    object LaunchImagePicker : UserProfileActions
    data class ShowImageConfirmationDialog(val uri: Uri) : UserProfileActions
    object DismissImageConfirmationDialog : UserProfileActions
    data class UpdateProfileImage(val uri: Uri) : UserProfileActions

    // Logout actions
    object ShowLogoutConfirmDialog : UserProfileActions
    object GoToChats : UserProfileActions
    object GoToSavedProducts : UserProfileActions
    object HideLogoutConfirmDialog : UserProfileActions
    object Logout : UserProfileActions

    // Error handling
    object ClearError : UserProfileActions
    object DismissMessage : UserProfileActions
}