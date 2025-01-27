package com.appdev.smartkisan.ViewModel

import android.content.Context
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
import com.appdev.smartkisan.Utils.SessionManagement
import com.appdev.smartkisan.data.UserEntity
import com.appdev.smartkisan.data.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    val repository: Repository,
    val supabaseClient: SupabaseClient
) :
    ViewModel() {

    var loginState by mutableStateOf(UserAuthState())
        private set
    private val _userInfoState = MutableStateFlow<ResultState<UserEntity>>(ResultState.Loading)
    val userInfoState: StateFlow<ResultState<UserEntity>> get() = _userInfoState

    private var _userInfo = MutableStateFlow(UserInfo())
    val userInfo: StateFlow<UserInfo> get() = _userInfo




    fun refreshToken(token: String, saveNewToken: (String?) -> Unit) {
        viewModelScope.launch {
            repository.refreshUserInfo(token) {
                getUserEntity()
                saveNewToken(getAccessToken())
            }
        }
    }

    fun getUserEntity() {
        viewModelScope.launch {
            repository.fetchUserInfo().collect { result ->
                _userInfoState.value = when (result) {
                    is ResultState.Failure -> ResultState.Failure(result.msg)
                    ResultState.Loading -> ResultState.Loading
                    is ResultState.Success -> ResultState.Success(result.data)
                }
            }
        }
    }


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
                    verifyOtp(action.code, action.number).collect { result ->
                        when (result) {
                            is ResultState.Failure -> loginState =
                                loginState.copy(errorMessage = result.msg.localizedMessage)

                            ResultState.Loading -> loginState = loginState.copy(isLoading = true)
                            is ResultState.Success -> {
                                if (getCurrentUserId() != null && getAccessToken() != null) {
                                    loginState =
                                        loginState.copy(
                                            isOtpVerified = true,
                                            isLoading = false,
                                            userId = getCurrentUserId()!!,
                                            accessToken = getAccessToken()!!
                                        )
                                }
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

            is PhoneAuthAction.SaveUserProfile -> {
                // take image , number and username form login state and save to db
                viewModelScope.launch {
                    repository.insertUser(
                        UserEntity(
                            name = loginState.userName,
                            contact = loginState.countryCode + loginState.phoneNumber,
                            imageUrl = "", role = loginState.userType
                        ), action.imageByteArray, action.imageUri
                    ).collect { result ->
                        loginState = when (result) {
                            is ResultState.Failure -> loginState.copy(errorMessage = result.msg.localizedMessage)
                            ResultState.Loading -> loginState.copy(isLoading = true)
                            is ResultState.Success -> {
                                loginState.copy(dataSaved = true, isLoading = false)
                            }
                        }
                    }
                }
            }

            is PhoneAuthAction.UpdatedUserType -> {
                loginState = loginState.copy(userType = action.type)
            }
        }
    }

    private fun getCurrentUserId(): String? {
        return supabaseClient.auth.currentUserOrNull()?.id
    }

    private fun getAccessToken(): String? {
        val accessToken = supabaseClient.auth.currentAccessTokenOrNull()
        return accessToken
    }

    fun createUserWithPhone(
        mobile: String
    ) = repository.signUpUserWithSupaBase(mobile)

    fun verifyOtp(
        code: String,
        phoneNumber: String
    ) = repository.verifyOtp(phoneNumber, code)
}