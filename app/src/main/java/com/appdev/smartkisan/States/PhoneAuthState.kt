package com.appdev.smartkisan.States

data class PhoneAuthState(
    val phoneNumber: String = "",
    val countryCode: String = "+92",
    val isPhoneNumberValid: Boolean = false,
    val otp: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)
