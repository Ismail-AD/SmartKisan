package com.appdev.smartkisan.ViewModel

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Actions.PhoneAuthAction
import com.appdev.smartkisan.Repository.Repository
import com.appdev.smartkisan.States.PhoneAuthState
import com.appdev.smartkisan.Utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    var loginState by mutableStateOf(PhoneAuthState())
        private set


    fun onAction(action: PhoneAuthAction) {
        when (action) {
            is PhoneAuthAction.SendMeOtp -> {
                viewModelScope.launch {
                    createUserWithPhone(action.number).collect { result ->
                        loginState = when (result) {
                            is ResultState.Failure -> loginState.copy(errorMessage = result.msg.localizedMessage)
                            ResultState.Loading -> loginState.copy(isLoading = true)
                            is ResultState.Success -> {
                                Log.d("Repository", "AT VIEWMODEL : SUCCESS")
                                loginState.copy(otpRequestAccepted = true)
                            }
                        }
                    }
                }
            }

            is PhoneAuthAction.numebrChange -> {
                loginState = loginState.copy(phoneNumber = action.number)
            }

            PhoneAuthAction.GoBack -> {

            }

            PhoneAuthAction.NextScreen -> {

            }

            is PhoneAuthAction.VerifyOtp -> {
                viewModelScope.launch {
                    verifyOtp(action.code,action.number).collect { result ->
                        loginState = when (result) {
                            is ResultState.Failure -> loginState.copy(errorMessage = result.msg.localizedMessage)
                            ResultState.Loading -> loginState.copy(isLoading = true)
                            is ResultState.Success -> {
                                Log.d("Repository", "AT VIEWMODEL : SUCCESS")
                                loginState.copy(isOtpVerified = true)
                            }
                        }
                    }
                }
            }

            is PhoneAuthAction.otpChange -> {
                loginState = loginState.copy(otp = action.updatedCode)
            }
        }
    }

    fun createUserWithPhone(
        mobile: String
    ) = repository.signUpUserWithSupaBase(mobile)

    fun verifyOtp(
        code: String,
        phoneNumber: String
    ) = repository.verifyOtp(phoneNumber, code)
}