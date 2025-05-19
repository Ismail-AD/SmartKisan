package com.appdev.smartkisan.presentation.feature.auth.forgetpassword

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.appdev.smartkisan.presentation.feature.auth.login.UserAuthAction
import com.appdev.smartkisan.R
import com.appdev.smartkisan.presentation.feature.auth.login.UserAuthState
import com.appdev.smartkisan.presentation.feature.auth.login.LoginViewModel
import com.appdev.smartkisan.presentation.Resuable.CustomButton
import com.appdev.smartkisan.presentation.theme.buttonColor

@Composable
fun ForgotPasswordRoot(
    loginViewModel: LoginViewModel,
    navigateToLogin: () -> Unit,
    navigateToResetPassword: () -> Unit
) {
    ForgotPassword(
        loginState = loginViewModel.loginState,
        onAction = { action ->
            when (action) {
                is UserAuthAction.GoBack -> {
                    navigateToLogin()
                }
                else -> loginViewModel.onAction(action)
            }
        },
        navigateToResetPassword = navigateToResetPassword
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPassword(
    loginState: UserAuthState,
    onAction: (UserAuthAction) -> Unit,
    navigateToResetPassword: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(loginState.errorMessage) {
        loginState.errorMessage?.let { error ->
            onAction(UserAuthAction.ModifyToastState(Pair(true, error)))
            onAction(UserAuthAction.ClearValidationError)
        }
    }

    LaunchedEffect(loginState.validationError) {
        loginState.validationError?.let { error ->
            onAction(UserAuthAction.ModifyToastState(Pair(true, error)))
            onAction(UserAuthAction.ClearValidationError)
        }
    }

    LaunchedEffect(loginState.passwordResetEmailSent) {
        if (loginState.passwordResetEmailSent) {
            // Navigate to OTP screen or wait for user to click on the link
            // For simplicity, here we show a success message
            onAction(
                UserAuthAction.ModifyToastState(
                Pair(true, "Password reset link sent. Please check your email.")
            ))
            navigateToResetPassword()
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
                    Dialog(onDismissRequest = { }) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    color = Color.Black,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.tempicon), contentDescription = "")

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Forgot Password",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Enter your email address to receive a password reset link",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Email Field
                OutlinedTextField(
                    value = loginState.email,
                    onValueChange = { onAction(UserAuthAction.EmailChange(it)) },
                    label = { Text("Email") },
                    placeholder = { Text("Enter email address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = buttonColor,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedLabelColor = buttonColor,
                        cursorColor = buttonColor
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Remember your password?",
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Login",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.clickable { onAction(UserAuthAction.GoBack) }
                    )
                }
            }

            CustomButton(
                onClick = {
                    onAction(UserAuthAction.ResetPasswordRequest(loginState.email))
                },
                text = "Send Reset Link",
                width = 1f
            )
        }

        if (loginState.showToastState.first) {
            Toast.makeText(context, loginState.showToastState.second, Toast.LENGTH_SHORT).show()
            onAction(UserAuthAction.ModifyToastState(Pair(false, "")))
        }
    }
}





