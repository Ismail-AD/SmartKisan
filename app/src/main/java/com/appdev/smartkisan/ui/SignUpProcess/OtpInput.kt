package com.appdev.smartkisan.ui.SignUpProcess

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.appdev.smartkisan.Actions.UserAuthAction
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.UserAuthState
import com.appdev.smartkisan.Utils.SessionManagement
import com.appdev.smartkisan.ViewModel.LoginViewModel
import com.appdev.smartkisan.ui.OtherComponents.CustomButton
import com.appdev.smartkisan.ui.OtherComponents.OtpTextField
import com.talhafaki.composablesweettoast.util.SweetToastUtil.SweetError
import kotlinx.coroutines.delay


@Composable
fun OtpInputRoot(
    navigateToNext: () -> Unit,
    loginViewModel: LoginViewModel,
    navigateUp: () -> Unit
) {
    OtpInput(
        email = loginViewModel.loginState.email,
        loginViewModel.loginState,
        onAction = { action ->
            when (action) {
                is UserAuthAction.NextScreen -> {
                    navigateToNext()
                }

                is UserAuthAction.GoBack -> {
                    navigateUp()
                }

                else -> loginViewModel.onAction(action)
            }
        })
}

@Composable
fun OtpInput(email: String, loginState: UserAuthState, onAction: (UserAuthAction) -> Unit) {

    val context = LocalContext.current


    var showToastState by remember { mutableStateOf(Pair(false, "")) }
    LaunchedEffect(key1 = loginState.isOtpVerified) {
        if (loginState.isOtpVerified) {

            val imageBytes = loginState.profileImage?.let { uri ->
                try {
                    context.contentResolver.openInputStream(uri)?.use {
                        it.readBytes()
                    }
                } catch (e: Exception) {
                    showToastState = Pair(true, "Failed to process image!")
                    null
                }
            }

            onAction.invoke(
                UserAuthAction.SaveUserProfile(
                    imageByteArray = imageBytes,
                    loginState.profileImage
                )
            )
        }
    }
    LaunchedEffect(loginState.errorMessage) {
        loginState.errorMessage?.let { error ->
            showToastState = Pair(true, error)
        }
    }
    LaunchedEffect(loginState.validationError) {
        loginState.validationError?.let { error ->
            showToastState = Pair(true, error)
            onAction(UserAuthAction.ClearValidationError)
        }
    }
    LaunchedEffect(key1 = loginState.dataSaved) {
        if (loginState.dataSaved) {
            loginState.userSession?.let { session ->
                SessionManagement.saveSession(
                    context = context,
                    accessToken = session.accessToken,
                    refreshToken = session.refreshToken,
                    expiresAt = session.expiresAt.epochSeconds,
                    userId = session.user?.id ?: "",
                    userEmail = session.user?.email ?: ""
                )
            }
            SessionManagement.saveUserType(
                context,
                userType = loginState.userType
            )
            onAction(UserAuthAction.NextScreen)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (loginState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Dialog(onDismissRequest = { /*TODO*/ }) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.tempicon), contentDescription = "")
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "OTP Verification",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = "Enter the OTP sent to",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = email,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }


                OtpTextField(
                    otpText = loginState.otp,
                    onOtpTextChange = { value, otpInputFilled ->
                        onAction(UserAuthAction.OtpChange(value))
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Don't receive the OTP ? ",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Resend OTP",
                        fontSize = 16.sp, textAlign = TextAlign.Center, color = Color(0xFF9747FF)
                    )
                }

            }
            CustomButton(
                onClick = { onAction(UserAuthAction.VerifyOtp(email = email, loginState.otp)) },
                text = "Verify", width = 1f
            )
        }
        if (showToastState.first) {
            Toast.makeText(context,showToastState.second,Toast.LENGTH_SHORT).show()
//            SweetError(
//                message = showToastState.second,
//                duration = Toast.LENGTH_SHORT,
//                padding = PaddingValues(top = 16.dp),
//                contentAlignment = Alignment.TopCenter
//            )
            showToastState = Pair(false, "")

        }
    }
}