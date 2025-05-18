package com.appdev.smartkisan.ViewModel

import android.net.Uri
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
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository,
    private val supabaseClient: SupabaseClient,
    private val sessionManagement: SessionManagement // Injected SessionManagement
) : ViewModel() {

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

    fun handleDeepLink(uri: Uri) {
        val fragment = uri.fragment

        if (fragment != null && fragment.contains("access_token=")) {
            // Parse the fragment to extract the access_token
            val tokenMap = fragment.split("&")
                .map { it.split("=", limit = 2) }
                .filter { it.size == 2 }
                .associate { it[0] to it[1] }

            val accessToken = tokenMap["access_token"]

            if (accessToken != null) {
                onAction(UserAuthAction.SetResetToken(accessToken))
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

    private fun validateResetPasswordEmailForm(): Pair<Boolean, String> {
        with(loginState) {
            return when {
                email.trim().isEmpty() -> {
                    Pair(false, "Please enter email address")
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Pair(false, "Please enter a valid email address")
                }

                else -> Pair(true, "Valid")
            }
        }
    }

    private fun validateNewPasswordForm(): Pair<Boolean, String> {
        with(loginState) {
            return when {
                newPassword.trim().isEmpty() -> {
                    Pair(false, "Please enter new password")
                }

                newPassword.length < 6 -> {
                    Pair(false, "Password should be at least 6 characters")
                }

                confirmNewPassword.trim().isEmpty() -> {
                    Pair(false, "Please confirm your new password")
                }

                newPassword != confirmNewPassword -> {
                    Pair(false, "Passwords do not match")
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

    private fun saveUserSession(
        session: UserSession,
        userName: String,
        userImage: String,
        userType: String
    ) {
        sessionManagement.saveSession(
            accessToken = session.accessToken,
            refreshToken = session.refreshToken,
            expiresAt = session.expiresAt.epochSeconds,
            userId = session.user?.id ?: "",
            userEmail = session.user?.email ?: ""
        )

        sessionManagement.saveUserName(
            userName = userName,
            userImage = userImage
        )

        sessionManagement.saveUserType(userType = userType)
    }


    fun onAction(action: UserAuthAction) {
        when (action) {
            is UserAuthAction.SendMeOtp -> {
                loginState = loginState.copy(isLoading = true)
                val validationResult = validateSignUpForm()
                if (validationResult.first) {
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
                // No implementation needed
            }

            UserAuthAction.NextScreen -> {
                // Handle this in the UI
            }

            is UserAuthAction.VerifyOtp -> {
                loginState = loginState.copy(isLoading = true)
                if (action.code.trim().isNotEmpty() && action.code.length == 6) {
                    viewModelScope.launch {
                        verifyEmail(action.email, action.code).collect { result ->
                            when (result) {
                                is ResultState.Failure -> loginState =
                                    loginState.copy(
                                        errorMessage = result.msg.localizedMessage,
                                        isLoading = false
                                    )

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
                    loginState = loginState.copy(
                        validationError = "Fill the otp fields first!",
                        isLoading = false
                    )
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
                // take image, number and username from login state and save to db
                viewModelScope.launch {
                    repository.insertUser(
                        UserEntity(
                            name = loginState.userName,
                            email = loginState.email,
                            imageUrl = "", role = loginState.userType
                        ), action.imageByteArray, action.imageUri
                    ).collect { result ->
                        loginState = when (result) {
                            is ResultState.Failure -> loginState.copy(
                                errorMessage = result.msg.localizedMessage,
                                isLoading = false
                            )

                            ResultState.Loading -> loginState.copy(isLoading = true)
                            is ResultState.Success -> {
                                val updatedState =
                                    loginState.copy(isLoading = false, imageUrl = result.data)
                                saveUserSession(
                                    session = loginState.userSession!!,
                                    userName = loginState.userName,
                                    userImage = result.data,
                                    userType = loginState.userType
                                )
                                updatedState
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
                // No implementation needed
            }

            is UserAuthAction.LoginUser -> {
                loginState = loginState.copy(isLoading = true)

                val validationResult = validateSignInForm()
                if (validationResult.first) {
                    viewModelScope.launch {
                        loginWithEmail(action.email, action.password).collect { result ->
                            when (result) {
                                is ResultState.Failure -> {
                                    Log.d("LOGIN", result.msg.localizedMessage)

                                    loginState =
                                        loginState.copy(
                                            errorMessage = result.msg.localizedMessage,
                                            isLoading = false
                                        )
                                }

                                ResultState.Loading -> loginState =
                                    loginState.copy(isLoading = true)

                                is ResultState.Success -> {
                                    loginState = loginState.copy(userSession = result.data)
                                    result.data?.let { fetchUserDetailsAndSaveSession(it) }
                                }
                            }
                        }
                    }
                } else {
                    loginState = loginState.copy(
                        validationError = validationResult.second,
                        isLoading = false
                    )
                }
            }

            is UserAuthAction.ResetPasswordRequest -> {
                loginState = loginState.copy(isLoading = true)
                val validationResult = validateResetPasswordEmailForm()

                if (validationResult.first) {
                    viewModelScope.launch {
                        repository.requestPasswordReset(action.email).collect { result ->
                            loginState = when (result) {
                                is ResultState.Failure -> loginState.copy(
                                    errorMessage = result.msg.localizedMessage,
                                    isLoading = false
                                )

                                ResultState.Loading -> loginState.copy(isLoading = true)
                                is ResultState.Success -> {
                                    loginState.copy(
                                        isLoading = false,
                                        passwordResetEmailSent = true,
                                        errorMessage = null
                                    )
                                }
                            }
                        }
                    }
                } else {
                    loginState = loginState.copy(
                        validationError = validationResult.second,
                        isLoading = false
                    )
                }
            }

            is UserAuthAction.NewPasswordChange -> {
                loginState = loginState.copy(newPassword = action.password)
            }

            is UserAuthAction.ConfirmNewPasswordChange -> {
                loginState = loginState.copy(confirmNewPassword = action.password)
            }

            is UserAuthAction.UpdatePassword -> {
                loginState = loginState.copy(isLoading = true)
                val validationResult = validateNewPasswordForm()

                if (validationResult.first) {
                    viewModelScope.launch {
                        repository.updateUserPassword(
                            action.password,
                            resetToken = loginState.resetToken,
                            refreshToken = loginState.refreshToken,
                            expiresIn = loginState.tokenExpiresIn, tokenType = loginState.tokenType, type = loginState.typeOfReset
                        ).collect { result ->
                            loginState = when (result) {
                                is ResultState.Failure -> loginState.copy(
                                    errorMessage = result.msg.localizedMessage,
                                    isLoading = false
                                )

                                ResultState.Loading -> loginState.copy(isLoading = true)
                                is ResultState.Success -> {
                                    loginState.copy(
                                        isLoading = false,
                                        passwordResetSuccess = true,
                                        errorMessage = null
                                    )
                                }
                            }
                        }
                    }
                } else {
                    loginState = loginState.copy(
                        validationError = validationResult.second,
                        isLoading = false
                    )
                }
            }

            UserAuthAction.SignUpScreen -> {
                // No implementation needed
            }

            UserAuthAction.ClearValidationError -> {
                loginState = loginState.copy(validationError = null, errorMessage = null)
            }

            UserAuthAction.ShowStoragePermissionDialog -> {
                loginState = loginState.copy(showStoragePermissionDialog = true)
            }

            UserAuthAction.DismissStoragePermissionDialog -> {
                loginState = loginState.copy(showStoragePermissionDialog = false)
            }

            is UserAuthAction.ModifyToastState -> {
                loginState = loginState.copy(showToastState = action.newState)
            }

            is UserAuthAction.SetResetToken -> {
                loginState = loginState.copy(
                    resetToken = action.token,
                    refreshToken = action.refreshToken,
                    tokenExpiresIn = action.expiresIn,
                    tokenType = action.resetTokenType,
                    typeOfReset = action.type,
                    deepLinkVerified = true
                )
            }

            else -> {}
        }
    }

    private fun fetchUserDetailsAndSaveSession(session: UserSession) {
        viewModelScope.launch {
            repository.fetchUserInfo().collect { userInformation ->
                when (userInformation) {
                    is ResultState.Failure -> {
                        loginState = loginState.copy(
                            isLoading = false,
                            errorMessage = userInformation.msg.localizedMessage
                        )
                    }

                    ResultState.Loading -> {
                        loginState = loginState.copy(isLoading = true)
                    }

                    is ResultState.Success -> {
                        val user = userInformation.data
                        saveUserSession(
                            session = session,
                            userName = user.name,
                            userImage = user.imageUrl ?: "",
                            userType = user.role
                        )
                        loginState = loginState.copy(
                            isLoading = false,
                            loginSuccess = true,
                            userType = user.role,
                            errorMessage = null,
                            userName = user.name,
                            imageUrl = user.imageUrl
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