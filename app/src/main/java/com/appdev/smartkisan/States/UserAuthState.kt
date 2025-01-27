package com.appdev.smartkisan.States

import android.net.Uri

data class UserAuthState(
    val phoneNumber: String = "",
    val countryCode: String = "+92",
    val isOtpValid: Boolean = false,
    val otp: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val otpRequestAccepted: Boolean = false,
    val isOtpVerified: Boolean = false,
    val userName: String = "",
    val userType: String = "Farmer",
    val profileImage: Uri? = null,
    val dataSaved: Boolean = false,
    var userId: String = "",
    var accessToken: String = ""
)
