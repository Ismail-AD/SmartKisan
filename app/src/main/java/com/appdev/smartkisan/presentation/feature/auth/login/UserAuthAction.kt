package com.appdev.smartkisan.presentation.feature.auth.login

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
    data class ModifyToastState(val newState: Pair<Boolean, String>) : UserAuthAction
    data class SaveUserProfile(val imageByteArray: ByteArray?, val imageUri: Uri?) : UserAuthAction

    object ShowStoragePermissionDialog : UserAuthAction
    object DismissStoragePermissionDialog : UserAuthAction

    data class HandleDeepLink(val uri: String) : UserAuthAction
    data class SetResetToken(
        val token: String,
        val refreshToken: String? = null,
        val expiresIn: Int? = null,
        val resetTokenType: String = "",
        val type: String = "",
    ) : UserAuthAction

    object ForgotPasswordScreen : UserAuthAction
    data class ResetPasswordRequest(val email: String) : UserAuthAction
    data class NewPasswordChange(val password: String) : UserAuthAction
    data class ConfirmNewPasswordChange(val password: String) : UserAuthAction
    data class UpdatePassword(val password: String) : UserAuthAction
}