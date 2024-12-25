package com.appdev.smartkisan.Actions

sealed interface PhoneAuthAction {
    data class numebrChange(val number: String) : PhoneAuthAction
    data object SendMeOtp : PhoneAuthAction
}