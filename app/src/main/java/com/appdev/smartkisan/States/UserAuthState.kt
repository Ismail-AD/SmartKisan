package com.appdev.smartkisan.States

import android.net.Uri
import io.github.jan.supabase.auth.user.UserSession

data class UserAuthState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val confirmPasswordVisible: Boolean = false,
    val passwordVisible: Boolean = false,
    val countryCode: String = "+92",
    val isOtpValid: Boolean = false,
    val loginSuccess: Boolean = false,
    val otp: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val otpRequestAccepted: Boolean = false,
    val isOtpVerified: Boolean = false,
    val userName: String = "",
    val userType: String = "Farmer",
    val profileImage: Uri? = null,
    val imageUrl: String? = null,
    val dataSaved: Boolean = false,
    var userId: String = "",
    val userSession: UserSession? = null,
    var accessToken: String = "",
    val validationError: String? = null,
    val showToastState: Pair<Boolean, String> = Pair(false, ""),
    val showStoragePermissionDialog: Boolean = false,
    val passwordResetEmailSent: Boolean = false,
    val passwordResetSuccess: Boolean = false,
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val deepLinkVerified: Boolean = false,
    val resetToken: String? = null,
    val refreshToken: String? = null, // Add this field
    val tokenExpiresIn: Int? = null,
    val tokenType: String="",
    val typeOfReset: String="",

)
