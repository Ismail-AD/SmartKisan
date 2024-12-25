package com.appdev.smartkisan.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.appdev.smartkisan.Actions.PhoneAuthAction
import com.appdev.smartkisan.States.PhoneAuthState
import dagger.hilt.android.lifecycle.HiltViewModel


@HiltViewModel
class LoginViewModel : ViewModel() {

    var loginState by mutableStateOf(PhoneAuthState())
        private set

    fun onAction(action: PhoneAuthAction) {
        when (action) {
            PhoneAuthAction.SendMeOtp -> TODO()
            is PhoneAuthAction.numebrChange -> {
                loginState = loginState.copy(phoneNumber = action.number)
            }
        }
    }
}