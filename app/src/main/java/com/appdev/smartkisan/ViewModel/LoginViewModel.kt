package com.appdev.smartkisan.ViewModel

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Actions.UserAuthAction
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

    private fun validateSignInForm(): Pair<Boolean, String> {
        with(loginState) {
            return when {
                email.trim().isEmpty() -> {
                    Pair(false, "Please enter email address")
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Pair(false, "Please enter a valid email address")
                }

                password.trim().isEmpty() -> {
                    Pair(false, "Please enter password")
                }

                password.length < 6 -> {
                    Pair(false, "Password should be at least 6 characters")
                }

                else -> Pair(true, "Valid")
            }
        }
    }

    private fun validateSignUpForm(): Pair<Boolean, String> {
        with(loginState) {
            return when {

                userName.trim().isEmpty() -> {
                    Pair(false, "Please enter your name")
                }

                userName.length < 3 -> {
                    Pair(false, "Name should be at least 3 characters")
                }

                email.trim().isEmpty() -> {
                    Pair(false, "Please enter email address")
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Pair(false, "Please enter a valid email address")
                }

                password.trim().isEmpty() -> {
                    Pair(false, "Please enter password")
                }

                password.length < 6 -> {
                    Pair(false, "Password should be at least 6 characters")
                }

                confirmPassword.trim().isEmpty() -> {
                    Pair(false, "Please confirm your password")
                }

                password != confirmPassword -> {
                    Pair(false, "Passwords do not match")
                }

                else -> Pair(true, "Valid")
            }
        }
    }


    fun onAction(action: UserAuthAction) {
        when (action) {
            is UserAuthAction.SendMeOtp -> {
                loginState = loginState.copy(isLoading = true)
                val validationResult = validateSignUpForm()
                Log.d("SignUp", "Result: ${validationResult}")
                if (validationResult.first) {
                    Log.d("SignUp", "IN IF")

                    viewModelScope.launch {
                        signUpWithEmail(action.email, action.password).collect { result ->
                            loginState = when (result) {
                                is ResultState.Failure -> loginState.copy(
                                    errorMessage = result.msg.localizedMessage,
                                    isLoading = false
                                )

                                ResultState.Loading -> loginState.copy(isLoading = true)
                                is ResultState.Success -> {
                                    loginState.copy(otpRequestAccepted = true, isLoading = false)
                                }
                            }
                        }
                    }
                } else {
                    Log.d("SignUp", "IN ELSE")
                    loginState = loginState.copy(
                        validationError = validationResult.second,
                        isLoading = false
                    )
                }
            }

            is UserAuthAction.EmailChange -> {
                loginState = loginState.copy(email = action.email)
            }

            UserAuthAction.GoBack -> {

            }

            UserAuthAction.NextScreen -> {

            }

            is UserAuthAction.VerifyOtp -> {
                loginState = loginState.copy(isLoading = true)
                if (action.code.trim().isNotEmpty() && action.code.length == 6) {
                    viewModelScope.launch {
                        verifyEmail(action.email, action.code).collect { result ->
                            when (result) {
                                is ResultState.Failure -> loginState =
                                    loginState.copy(errorMessage = result.msg.localizedMessage,isLoading = false)

                                ResultState.Loading -> loginState =
                                    loginState.copy(isLoading = true)

                                is ResultState.Success -> {
                                    result.data?.let { session ->
                                        if (getCurrentUserId() != null && getAccessToken() != null) {
                                            loginState =
                                                loginState.copy(
                                                    isOtpVerified = true,
                                                    userId = getCurrentUserId()!!,
                                                    accessToken = getAccessToken()!!,
                                                    userSession = session
                                                )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    loginState = loginState.copy(validationError = "Fill the otp fields first!",isLoading = false)

                }
            }

            is UserAuthAction.PasswordChange -> {
                loginState = loginState.copy(password = action.password)
            }

            is UserAuthAction.ConfirmPasswordChange -> {
                loginState = loginState.copy(confirmPassword = action.cpassword)
            }

            is UserAuthAction.OtpChange -> {
                loginState = loginState.copy(otp = action.updatedCode)
            }

            is UserAuthAction.SelectedImageUri -> {
                loginState = loginState.copy(profileImage = action.imageUri)
            }

            is UserAuthAction.Username -> {
                loginState = loginState.copy(userName = action.username)
            }

            is UserAuthAction.SaveUserProfile -> {
                // take image , number and username form login state and save to db
                viewModelScope.launch {
                    repository.insertUser(
                        UserEntity(
                            name = loginState.userName,
                            email = loginState.email,
                            imageUrl = "", role = loginState.userType
                        ), action.imageByteArray, action.imageUri
                    ).collect { result ->
                        loginState = when (result) {
                            is ResultState.Failure -> loginState.copy(errorMessage = result.msg.localizedMessage,isLoading = false)
                            ResultState.Loading -> loginState.copy(isLoading = true)
                            is ResultState.Success -> {
                                loginState.copy(dataSaved = true, isLoading = false)
                            }
                        }
                    }
                }
            }

            is UserAuthAction.UpdatedUserType -> {
                loginState = loginState.copy(userType = action.type)
            }

            is UserAuthAction.UpdateConfirmPasswordVisible -> {
                loginState = loginState.copy(confirmPasswordVisible = action.cpShow)

            }

            is UserAuthAction.UpdatePasswordVisible -> {
                loginState = loginState.copy(passwordVisible = action.show)

            }

            UserAuthAction.LoginScreen -> {

            }

            is UserAuthAction.LoginUser -> {
                loginState = loginState.copy(isLoading = true)

                val validationResult = validateSignInForm()
                if (validationResult.first) {
                    viewModelScope.launch {
                        loginWithEmail(action.email, action.password).collect { result ->
                            when (result) {
                                is ResultState.Failure -> {
                                    Log.d("LOGIN",result.msg.localizedMessage)

                                    loginState =
                                        loginState.copy(
                                            errorMessage = result.msg.localizedMessage,
                                            isLoading = false
                                        )
                                }

                                ResultState.Loading -> loginState =
                                    loginState.copy(isLoading = true)

                                is ResultState.Success -> {
                                    fetchUserDetails()
                                }
                            }
                        }
                    }
                } else {
                    loginState = loginState.copy(validationError = validationResult.second, isLoading = false)
                }
            }

            UserAuthAction.SignUpScreen -> {}
            UserAuthAction.ClearValidationError -> {
                loginState = loginState.copy(validationError = null)
            }
        }
    }

    private fun fetchUserDetails() {
        viewModelScope.launch {
            repository.fetchUserInfo().collect { userInformation ->
                loginState = when (userInformation) {
                    is ResultState.Failure -> {
                        Log.d("LOGIN",userInformation.msg.localizedMessage)
                        loginState.copy(
                            isLoading = false,
                            errorMessage = userInformation.msg.localizedMessage
                        )
                    }

                    ResultState.Loading -> {
                        loginState.copy(isLoading = true)
                    }

                    is ResultState.Success -> {
                        loginState.copy(
                            isLoading = false,
                            loginSuccess = true,
                            userType = userInformation.data.role,
                            errorMessage = null
                        )
                    }
                }
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

    private fun signUpWithEmail(email: String, password: String) =
        repository.signUpUserWithEmail(email, password)

    private fun loginWithEmail(email: String, password: String) =
        repository.loginUser(email, password)

    private fun verifyEmail(email: String, otp: String) =
        repository.verifyEmailAndSignIn(email, otp)
}