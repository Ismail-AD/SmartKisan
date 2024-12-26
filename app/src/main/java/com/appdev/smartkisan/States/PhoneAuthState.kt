package com.appdev.smartkisan.States

data class PhoneAuthState(
    val phoneNumber: String = "",
    val countryCode: String = "+92",
    val isOtpValid: Boolean = false,
    val otp: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val otpRequestAccepted:Boolean = false,
    val isOtpVerified:Boolean = false
)
