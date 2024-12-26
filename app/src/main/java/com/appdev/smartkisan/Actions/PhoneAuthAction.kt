package com.appdev.smartkisan.Actions

import android.app.Activity

sealed interface PhoneAuthAction {
    data class numebrChange(val number: String) : PhoneAuthAction
    data class otpChange(val updatedCode: String) : PhoneAuthAction
    data class SendMeOtp(val number: String,val activity:Activity) : PhoneAuthAction
    data class VerifyOtp(val number: String,val code:String) : PhoneAuthAction
    data object GoBack: PhoneAuthAction
    data object NextScreen: PhoneAuthAction
}