package com.appdev.smartkisan.presentation.feature.Seller.account


import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.data.repository.Repository
import com.appdev.smartkisan.presentation.feature.farmer.home.LocationPermissionState
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.Utils.SessionManagement
import com.google.android.gms.location.FusedLocationProviderClient
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
class SellerProfileViewModel @Inject constructor(
    private val sellerRepository: Repository,
    private val fusedLocationClient: FusedLocationProviderClient,
    val sessionManagement: SessionManagement,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _sellerProfileState = MutableStateFlow(SellerProfileState())
    val sellerProfileState: StateFlow<SellerProfileState> = _sellerProfileState.asStateFlow()

    private val TAG = "SellerProfileViewModel"

    init {
        fetchUserDetails()
    }

    fun onEvent(event: SellerProfileActions) {
        when (event) {
            is SellerProfileActions.SetTemporaryCurrentPassword -> setTemporaryCurrentPassword(event.password)
            is SellerProfileActions.LoadSellerProfile -> loadSellerProfile()
            is SellerProfileActions.ShowEditUsernameDialog -> showEditUsernameDialog()
            is SellerProfileActions.ShowEditShopNameDialog -> showEditShopNameDialog()
            is SellerProfileActions.ShowUpdatePasswordDialog -> showUpdatePasswordDialog()
            is SellerProfileActions.ShowLocationPicker -> showLocationPicker()
            is SellerProfileActions.DismissAllDialogs -> dismissAllDialogs()
            is SellerProfileActions.DismissLocationDialog -> dismissLocationDialog()
            is SellerProfileActions.Logout -> performLogout()
            SellerProfileActions.ShowLogoutConfirmDialog -> {
                _sellerProfileState.update { it.copy(showLogoutConfirmDialog = true) }
            }

            SellerProfileActions.HideLogoutConfirmDialog -> {
                _sellerProfileState.update { it.copy(showLogoutConfirmDialog = false) }
            }
            is SellerProfileActions.ClearError -> {
                _sellerProfileState.value = _sellerProfileState.value.copy(error = null)
            }
            is SellerProfileActions.DismissMessage -> {
                _sellerProfileState.value = _sellerProfileState.value.copy(successMessage = null)

            }

            is SellerProfileActions.UpdateUsername -> updateUsername(event.username)
            is SellerProfileActions.UpdateShopName -> updateShopName(event.shopName)
            is SellerProfileActions.UpdatePassword -> updatePassword(
                sellerProfileState.value.userEntity.email,
                event.currentPassword,
                event.password,
                event.confirmPassword
            )

            is SellerProfileActions.ShowImageConfirmationDialog -> showImageConfirmationDialog(event.uri)
            is SellerProfileActions.DismissImageConfirmationDialog -> dismissImageConfirmationDialog()

            is SellerProfileActions.UpdateShopLocation -> updateShopLocation(event.lat, event.long)
            is SellerProfileActions.UpdateLocationPermission -> updateLocationPermissionState(event.state)
            is SellerProfileActions.UpdateProfileImage -> updateProfileImage(event.uri)

            is SellerProfileActions.ShowEditContactDialog -> showEditContactDialog()
            is SellerProfileActions.SetTemporaryContact -> setTemporaryContact(event.contact)
            is SellerProfileActions.UpdateContact -> updateContact(event.contact)

            is SellerProfileActions.SetTemporaryUsername -> setTemporaryUsername(event.username)
            is SellerProfileActions.SetTemporaryShopName -> setTemporaryShopName(event.shopName)
            is SellerProfileActions.SetTemporaryPassword -> setTemporaryPassword(event.password)
            is SellerProfileActions.SetTemporaryConfirmPassword -> setTemporaryConfirmPassword(event.password)
            else -> {
                // Handle other actions if needed
            }
        }
    }

    private fun performLogout() {
        viewModelScope.launch {
            _sellerProfileState.update { it.copy(isLoading = true) }

            sellerRepository.logout().collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {

                        sessionManagement.clearSession()

                        // Update state to show a success message before navigation
                        _sellerProfileState.update {
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
                        _sellerProfileState.update {
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


    fun clearFieldErrors() {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            usernameError = null,
            shopNameError = null,
            contactError = null,
            currentPasswordError = null,
            newPasswordError = null,
            confirmPasswordError = null,
            error = null
        )
    }

    private fun showLocationPicker() {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            showLocationPickerMap = true
        )
    }

    private fun showImageConfirmationDialog(uri: Uri) {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            showImageConfirmationDialog = true,
            selectedImageUri = uri
        )
    }

    private fun dismissImageConfirmationDialog() {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            showImageConfirmationDialog = false,
            selectedImageUri = null
        )
    }

    private fun updateContact(contact: String) {
        clearFieldErrors() // Clear previous errors

        if (contact.isBlank()) {
            _sellerProfileState.value = _sellerProfileState.value.copy(
                contactError = "Contact information cannot be empty"
            )
            return
        }

        // Validate phone number
        if (!isValidPhoneNumber(contact)) {
            _sellerProfileState.value = _sellerProfileState.value.copy(
                contactError = "Please enter a valid phone number"
            )
            return
        }

        viewModelScope.launch {
            sellerRepository.updateContact(contact).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(isLoading = true)
                    }

                    is ResultState.Success -> {
                        val updatedSeller = _sellerProfileState.value.seller.copy(contact = contact)
                        _sellerProfileState.value = _sellerProfileState.value.copy(
                            seller = updatedSeller,
                            isLoading = false,
                            error = null,
                            showEditContactDialog = false,
                            successMessage = "Contact updated successfully"
                        )
                    }

                    is ResultState.Failure -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(
                            isLoading = false,
                            contactError = parseUserFriendlyError(
                                result.msg.localizedMessage ?: "",
                                "contact"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun showEditContactDialog() {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            showEditContactDialog = true,
            temporaryContact = _sellerProfileState.value.seller.contact
        )
    }

    private fun setTemporaryContact(contact: String) {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            temporaryContact = contact
        )
    }


    private fun setTemporaryCurrentPassword(password: String) {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            temporaryCurrentPassword = password
        )
    }

    private fun loadSellerProfile() {
        viewModelScope.launch {
            sellerRepository.fetchSellerMetaData().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(isLoading = true)
                    }

                    is ResultState.Success -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(
                            isLoading = false,
                            seller = result.data,
                            error = null
                        )
                    }

                    is ResultState.Failure -> {
                        val errorMessage = result.msg.localizedMessage
                        if (errorMessage != null && !errorMessage.contains("List is empty.")) {
                            _sellerProfileState.value = _sellerProfileState.value.copy(
                                isLoading = false,
                                error = errorMessage
                            )
                        } else {
                            _sellerProfileState.value = _sellerProfileState.value.copy(
                                isLoading = false
                                // No error is set when the list is empty
                            )
                        }
                    }
                }
            }
        }
    }

    private fun fetchUserDetails() {
        viewModelScope.launch {
            sellerRepository.fetchUserInfo().collect { userInformation ->
                when (userInformation) {
                    is ResultState.Failure -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(
                            error = userInformation.msg.localizedMessage
                        )
                        loadSellerProfile()
                    }

                    ResultState.Loading -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(isLoading = true)
                    }

                    is ResultState.Success -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(
                            error = null,
                            userEntity = userInformation.data
                        )
                        loadSellerProfile()
                    }
                }
            }
        }
    }

    private fun showEditUsernameDialog() {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            showEditUsernameDialog = true,
            temporaryUsername = _sellerProfileState.value.userEntity.name
        )
    }

    private fun showEditShopNameDialog() {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            showEditShopNameDialog = true,
            temporaryShopName = _sellerProfileState.value.seller.shopName
        )
    }

    private fun showUpdatePasswordDialog() {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            showUpdatePasswordDialog = true,
            temporaryPassword = "",
            temporaryConfirmPassword = ""
        )
    }

    private fun dismissAllDialogs() {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            showEditUsernameDialog = false,
            showEditShopNameDialog = false,
            showEditContactDialog = false,
            showUpdatePasswordDialog = false,
            showLocationPickerMap = false
        )
    }

    private fun dismissLocationDialog() {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            showLocationDialog = false
        )
    }

    private fun updateUsername(username: String) {
        clearFieldErrors() // Clear previous errors

        if (username.isBlank()) {
            _sellerProfileState.value = _sellerProfileState.value.copy(
                usernameError = "Username cannot be empty" // Field-specific error
            )
            return
        }
        viewModelScope.launch {
            sellerRepository.updateUsername(username).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(isLoading = true)
                    }

                    is ResultState.Success -> {
                        sessionManagement.saveUserName(username)
                        _sellerProfileState.value = _sellerProfileState.value.copy(
                            userEntity = _sellerProfileState.value.userEntity.copy(name = username),
                            isLoading = false,
                            error = null,
                            showEditUsernameDialog = false,
                            successMessage = "Username updated successfully"
                        )
                    }

                    is ResultState.Failure -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(
                            isLoading = false,
                            usernameError = parseUserFriendlyError(
                                result.msg.localizedMessage ?: "", "name"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // Basic validation: Check if phone number contains only digits, '+', '-', ' ' characters
        // and has a reasonable length (typically between 8 and 15 digits)
        val phoneRegex = Regex("^[+]?[0-9 -]{8,15}$")

        // Ensure phone number contains primarily digits (with optional formatting characters)
        val digitCount = phoneNumber.count { it.isDigit() }

        return phoneRegex.matches(phoneNumber) && digitCount >= 8
    }

    private fun updateShopName(shopName: String) {
        clearFieldErrors() // Clear previous errors
        if (shopName.isBlank()) {
            _sellerProfileState.value = _sellerProfileState.value.copy(
                shopNameError = "Shop name cannot be empty"
            )
            return
        }
        viewModelScope.launch {
            sellerRepository.updateShopName(shopName).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(isLoading = true)
                    }

                    is ResultState.Success -> {
                        val updatedSeller =
                            _sellerProfileState.value.seller.copy(shopName = shopName)
                        _sellerProfileState.value = _sellerProfileState.value.copy(
                            seller = updatedSeller,
                            isLoading = false,
                            error = null,
                            showEditShopNameDialog = false,
                            successMessage = "Shop name updated successfully"
                        )
                    }

                    is ResultState.Failure -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(
                            isLoading = false,
                            shopNameError = parseUserFriendlyError(
                                result.msg.localizedMessage ?: "", "shopName"
                            )
                        )
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
            _sellerProfileState.value = _sellerProfileState.value.copy(
                currentPasswordError = "Current password cannot be empty"
            )
            return
        }

        // Validate new password
        if (password.isBlank()) {
            _sellerProfileState.value = _sellerProfileState.value.copy(
                newPasswordError = "New password cannot be empty"
            )
            return
        }

        if (password != confirmPassword) {
            _sellerProfileState.value = _sellerProfileState.value.copy(
                confirmPasswordError = "Passwords do not match"
            )
            return
        }

        viewModelScope.launch {
            sellerRepository.updatePassword(email, currentPassword, password).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(isLoading = true)
                    }

                    is ResultState.Success -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(
                            isLoading = false,
                            error = null,
                            showUpdatePasswordDialog = false,
                            temporaryPassword = "",
                            temporaryConfirmPassword = "",
                            successMessage = "Password updated successfully"
                        )
                    }

                    is ResultState.Failure -> {
                        val errorMessage = result.msg.localizedMessage
                        if (errorMessage != null) {
                            if (errorMessage.contains("Invalid login credentials")) {
                                _sellerProfileState.value = _sellerProfileState.value.copy(
                                    isLoading = false,
                                    currentPasswordError = "Current password is incorrect"
                                )
                            } else {
                                _sellerProfileState.value = _sellerProfileState.value.copy(
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

    private fun updateShopLocation(lat: Double, long: Double) {
        viewModelScope.launch {
            sellerRepository.updateShopLocation(lat, long).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(isLoading = true)
                    }

                    is ResultState.Success -> {
                        val updatedSeller =
                            _sellerProfileState.value.seller.copy(latitude = lat, longitude = long)
                        _sellerProfileState.value = _sellerProfileState.value.copy(
                            seller = updatedSeller,
                            isLoading = false,
                            error = null,
                            showLocationPickerMap = false,
                            successMessage = "Shop location updated successfully"
                        )
                    }

                    is ResultState.Failure -> {
                        _sellerProfileState.value = _sellerProfileState.value.copy(
                            isLoading = false,
                            error = result.msg.localizedMessage
                        )
                    }
                }
            }
        }
    }


    private fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            try {
                // Convert Uri to ByteArray - you would need to implement this
                val imageByteArray = getByteArrayFromUri(uri)

                sellerRepository.updateProfileImage(uri, imageByteArray).collect { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            _sellerProfileState.value =
                                _sellerProfileState.value.copy(isLoading = true)
                        }

                        is ResultState.Success -> {
                            sessionManagement.saveUserImage(result.data)
                            val updatedSeller =
                                _sellerProfileState.value.userEntity.copy(imageUrl = result.data)
                            _sellerProfileState.value = _sellerProfileState.value.copy(
                                userEntity = updatedSeller,
                                isLoading = false,
                                error = null,
                                selectedImageUri = null,
                                successMessage = "Image Uploaded Successfully"
                            )
                        }

                        is ResultState.Failure -> {
                            _sellerProfileState.value = _sellerProfileState.value.copy(
                                isLoading = false,
                                error = result.msg.localizedMessage
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _sellerProfileState.value = _sellerProfileState.value.copy(
                    isLoading = false,
                    error = "Failed to process image: ${e.message}"
                )
            }
        }
    }

    // Helper function to convert Uri to ByteArray
    private fun getByteArrayFromUri(uri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
        return inputStream?.readBytes() ?: ByteArray(0)
    }

    private fun parseUserFriendlyError(errorMessage: String, field: String): String {
        // Check for constraint violation errors
        return when {
            // Username constraint violation
            (field == "name" && errorMessage.contains("constraint") && errorMessage.contains("unique")) ||
                    (errorMessage.contains("violates unique constraint") && errorMessage.contains("users_name_key")) -> {
                "This username is already taken. Please choose a different username."
            }

            // Shop name constraint violation
            (field == "shopName" && errorMessage.contains("constraint") && errorMessage.contains("unique")) ||
                    (errorMessage.contains("violates unique constraint") && errorMessage.contains("shop_name_key")) -> {
                "This shop name is already taken. Please choose a different shop name."
            }

            // Contact constraint violation
            (field == "contact" && errorMessage.contains("constraint") && errorMessage.contains("unique")) ||
                    (errorMessage.contains("violates unique constraint") && errorMessage.contains("contact_key")) -> {
                "This contact information is already in use. Please provide a different contact."
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

    private fun updateLocationPermissionState(state: LocationPermissionState) {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            locationPermissionState = state,
            showLocationDialog = true
        )
    }

    private fun setTemporaryUsername(username: String) {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            temporaryUsername = username
        )
    }

    private fun setTemporaryShopName(shopName: String) {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            temporaryShopName = shopName
        )
    }

    private fun setTemporaryPassword(password: String) {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            temporaryPassword = password
        )
    }

    private fun setTemporaryConfirmPassword(password: String) {
        _sellerProfileState.value = _sellerProfileState.value.copy(
            temporaryConfirmPassword = password
        )
    }
}