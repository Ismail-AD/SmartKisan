package com.appdev.smartkisan.ViewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Actions.PhoneAuthAction
import com.appdev.smartkisan.Repository.Repository
import com.appdev.smartkisan.States.UserAuthState
import com.appdev.smartkisan.Utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    var loginState by mutableStateOf(UserAuthState())
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
                                loginState.copy(isOtpVerified = true)
                            }
                        }
                    }
                }
            }

            is PhoneAuthAction.otpChange -> {
                loginState = loginState.copy(otp = action.updatedCode)
            }

            is PhoneAuthAction.SelectedImageUri -> {
                loginState = loginState.copy(profileImage = action.imageUri)
            }
            is PhoneAuthAction.Username -> {
                loginState = loginState.copy(userName = action.username)
            }

            PhoneAuthAction.SaveUserProfile -> {
                // take image , number and username form login state and save to db
            }

            is PhoneAuthAction.UpdatedUserType -> {
                loginState = loginState.copy(userType = action.type)
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