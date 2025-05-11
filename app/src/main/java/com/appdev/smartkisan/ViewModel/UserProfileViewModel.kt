package com.appdev.smartkisan.ViewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Actions.UserProfileActions
import com.appdev.smartkisan.Repository.Repository
import com.appdev.smartkisan.States.UserProfileState
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.Utils.SessionManagement
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: Repository,
    val sessionManagement: SessionManagement,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _userProfileState = MutableStateFlow(UserProfileState())
    val userProfileState: StateFlow<UserProfileState> = _userProfileState.asStateFlow()

    private val TAG = "UserProfileViewModel"

    init {
        fetchUserDetails()
    }

    fun onEvent(event: UserProfileActions) {
        when (event) {
            is UserProfileActions.LoadUserProfile -> fetchUserDetails()
            is UserProfileActions.ShowEditUsernameDialog -> showEditUsernameDialog()
            is UserProfileActions.ShowUpdatePasswordDialog -> showUpdatePasswordDialog()
            is UserProfileActions.DismissAllDialogs -> dismissAllDialogs()
            is UserProfileActions.Logout -> performLogout()
            is UserProfileActions.ShowLogoutConfirmDialog -> {
                _userProfileState.update { it.copy(showLogoutConfirmDialog = true) }
            }
            is UserProfileActions.HideLogoutConfirmDialog -> {
                _userProfileState.update { it.copy(showLogoutConfirmDialog = false) }
            }
            is UserProfileActions.ClearError -> {
                _userProfileState.update { it.copy(error = null) }
            }
            is UserProfileActions.DismissMessage -> {
                _userProfileState.update { it.copy(successMessage = null) }
            }
            is UserProfileActions.UpdateUsername -> updateUsername(event.username)
            is UserProfileActions.UpdatePassword -> updatePassword(
                userProfileState.value.userEntity.email,
                event.currentPassword,
                event.password,
                event.confirmPassword
            )
            is UserProfileActions.ShowImageConfirmationDialog -> showImageConfirmationDialog(event.uri)
            is UserProfileActions.DismissImageConfirmationDialog -> dismissImageConfirmationDialog()
            is UserProfileActions.UpdateProfileImage -> updateProfileImage(event.uri)
            is UserProfileActions.SetTemporaryUsername -> setTemporaryUsername(event.username)
            is UserProfileActions.SetTemporaryCurrentPassword -> setTemporaryCurrentPassword(event.password)
            is UserProfileActions.SetTemporaryPassword -> setTemporaryPassword(event.password)
            is UserProfileActions.SetTemporaryConfirmPassword -> setTemporaryConfirmPassword(event.password)
            else -> {
                // Handle other actions if needed
            }
        }
    }

    private fun performLogout() {
        viewModelScope.launch {
            _userProfileState.update { it.copy(isLoading = true) }

            repository.logout().collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {
                        sessionManagement.clearSession()
                        _userProfileState.update {
                            it.copy(
                                successMessage = "Logged out successfully",
                                showLogoutConfirmDialog = false,
                                isLoading = false,
                                isLogoutConfirmed = true
                            )
                        }
                    }
                    is ResultState.Failure -> {
                        sessionManagement.clearSession()
                        _userProfileState.update {
                            it.copy(
                                error = "Logout failed: ${result.msg.localizedMessage}",
                                showLogoutConfirmDialog = false,
                                isLoading = false
                            )
                        }
                    }
                    is ResultState.Loading -> {
                        // Already showing loading state
                    }
                }
            }
        }
    }

    private fun clearFieldErrors() {
        _userProfileState.update {
            it.copy(
                usernameError = null,
                currentPasswordError = null,
                newPasswordError = null,
                confirmPasswordError = null,
                error = null
            )
        }
    }

    private fun showImageConfirmationDialog(uri: Uri) {
        _userProfileState.update {
            it.copy(
                showImageConfirmationDialog = true,
                selectedImageUri = uri
            )
        }
    }

    private fun dismissImageConfirmationDialog() {
        _userProfileState.update {
            it.copy(
                showImageConfirmationDialog = false,
                selectedImageUri = null
            )
        }
    }

    private fun fetchUserDetails() {
        viewModelScope.launch {
            repository.fetchUserInfo().collect { result ->
                when (result) {
                    is ResultState.Failure -> {
                        _userProfileState.update {
                            it.copy(
                                error = result.msg.localizedMessage,
                                isLoading = false
                            )
                        }
                    }
                    ResultState.Loading -> {
                        _userProfileState.update { it.copy(isLoading = true) }
                    }
                    is ResultState.Success -> {
                        _userProfileState.update {
                            it.copy(
                                error = null,
                                userEntity = result.data,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showEditUsernameDialog() {
        _userProfileState.update {
            it.copy(
                showEditUsernameDialog = true,
                temporaryUsername = _userProfileState.value.userEntity.name
            )
        }
    }

    private fun showUpdatePasswordDialog() {
        _userProfileState.update {
            it.copy(
                showUpdatePasswordDialog = true,
                temporaryCurrentPassword = "",
                temporaryPassword = "",
                temporaryConfirmPassword = ""
            )
        }
    }

    private fun dismissAllDialogs() {
        _userProfileState.update {
            it.copy(
                showEditUsernameDialog = false,
                showUpdatePasswordDialog = false,
                showImageConfirmationDialog = false
            )
        }
    }

    private fun updateUsername(username: String) {
        clearFieldErrors() // Clear previous errors

        if (username.isBlank()) {
            _userProfileState.update {
                it.copy(usernameError = "Username cannot be empty")
            }
            return
        }

        viewModelScope.launch {
            repository.updateUsername(username).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _userProfileState.update { it.copy(isLoading = true) }
                    }
                    is ResultState.Success -> {
                        sessionManagement.saveUserName(username)
                        _userProfileState.update {
                            it.copy(
                                userEntity = it.userEntity.copy(name = username),
                                isLoading = false,
                                error = null,
                                showEditUsernameDialog = false,
                                successMessage = "Username updated successfully"
                            )
                        }
                    }
                    is ResultState.Failure -> {
                        _userProfileState.update {
                            it.copy(
                                isLoading = false,
                                usernameError = parseUserFriendlyError(
                                    result.msg.localizedMessage ?: "",
                                    "name"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updatePassword(
        email: String,
        currentPassword: String,
        password: String,
        confirmPassword: String
    ) {
        clearFieldErrors() // Clear previous errors

        if (currentPassword.isBlank()) {
            _userProfileState.update {
                it.copy(currentPasswordError = "Current password cannot be empty")
            }
            return
        }

        // Validate new password
        if (password.isBlank()) {
            _userProfileState.update {
                it.copy(newPasswordError = "New password cannot be empty")
            }
            return
        }

        if (password != confirmPassword) {
            _userProfileState.update {
                it.copy(confirmPasswordError = "Passwords do not match")
            }
            return
        }

        viewModelScope.launch {
            repository.updatePassword(email, currentPassword, password).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _userProfileState.update { it.copy(isLoading = true) }
                    }
                    is ResultState.Success -> {
                        _userProfileState.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                                showUpdatePasswordDialog = false,
                                temporaryPassword = "",
                                temporaryConfirmPassword = "",
                                successMessage = "Password updated successfully"
                            )
                        }
                    }
                    is ResultState.Failure -> {
                        val errorMessage = result.msg.localizedMessage
                        if (errorMessage != null) {
                            if (errorMessage.contains("Invalid login credentials")) {
                                _userProfileState.update {
                                    it.copy(
                                        isLoading = false,
                                        currentPasswordError = "Current password is incorrect"
                                    )
                                }
                            } else {
                                _userProfileState.update {
                                    it.copy(
                                        isLoading = false,
                                        error = parseUserFriendlyError(errorMessage, "password")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            try {
                // Convert Uri to ByteArray
                val imageByteArray = getByteArrayFromUri(uri)

                repository.updateProfileImage(uri, imageByteArray).collect { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            _userProfileState.update { it.copy(isLoading = true) }
                        }
                        is ResultState.Success -> {
                            sessionManagement.saveUserImage(result.data)
                            _userProfileState.update {
                                it.copy(
                                    userEntity = it.userEntity.copy(imageUrl = result.data),
                                    isLoading = false,
                                    error = null,
                                    selectedImageUri = null,
                                    showImageConfirmationDialog = false,
                                    successMessage = "Profile image updated successfully"
                                )
                            }
                        }
                        is ResultState.Failure -> {
                            _userProfileState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.msg.localizedMessage
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _userProfileState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to process image: ${e.message}"
                    )
                }
            }
        }
    }

    // Helper function to convert Uri to ByteArray
    private fun getByteArrayFromUri(uri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
        return inputStream?.readBytes() ?: ByteArray(0)
    }

    private fun parseUserFriendlyError(errorMessage: String, field: String): String {
        return when {
            // Username constraint violation
            (field == "name" && errorMessage.contains("constraint") && errorMessage.contains("unique")) ||
                    (errorMessage.contains("violates unique constraint") && errorMessage.contains("users_name_key")) -> {
                "This username is already taken. Please choose a different username."
            }
            // Authentication errors
            errorMessage.contains("Invalid login credentials") -> {
                "Invalid password. Please check your current password and try again."
            }
            // Network errors
            errorMessage.contains("Unable to resolve host") || errorMessage.contains("timeout") -> {
                "Network error. Please check your internet connection and try again."
            }
            // Default case for other errors
            else -> {
                "An error occurred while updating your $field. Please try again later."
            }
        }
    }

    private fun setTemporaryUsername(username: String) {
        _userProfileState.update { it.copy(temporaryUsername = username) }
    }

    private fun setTemporaryCurrentPassword(password: String) {
        _userProfileState.update { it.copy(temporaryCurrentPassword = password) }
    }

    private fun setTemporaryPassword(password: String) {
        _userProfileState.update { it.copy(temporaryPassword = password) }
    }

    private fun setTemporaryConfirmPassword(password: String) {
        _userProfileState.update { it.copy(temporaryConfirmPassword = password) }
    }
}