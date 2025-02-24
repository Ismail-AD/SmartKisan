package com.appdev.smartkisan.Actions

import android.app.Activity
import android.net.Uri

sealed interface UserAuthAction {
    data class EmailChange(val email: String) : UserAuthAction
    data class PasswordChange(val password: String) : UserAuthAction
    data class ConfirmPasswordChange(val cpassword: String) : UserAuthAction
    data class UpdatedUserType(val type: String) : UserAuthAction
    data class UpdateConfirmPasswordVisible(val cpShow: Boolean) : UserAuthAction
    data class UpdatePasswordVisible(val show: Boolean) : UserAuthAction
    data class SelectedImageUri(val imageUri: Uri?) : UserAuthAction
    data class Username(val username: String) : UserAuthAction
    data class OtpChange(val updatedCode: String) : UserAuthAction
    data object GoBack : UserAuthAction
    data object NextScreen : UserAuthAction
    data object LoginScreen : UserAuthAction
    data object SignUpScreen : UserAuthAction
    data object ClearValidationError : UserAuthAction
    data class SendMeOtp(val email: String, val password: String) : UserAuthAction
    data class LoginUser(val email: String, val password: String) : UserAuthAction
    data class VerifyOtp(val email: String, val code: String) : UserAuthAction
    data class SaveUserProfile(val imageByteArray: ByteArray?, val imageUri: Uri?) : UserAuthAction
}