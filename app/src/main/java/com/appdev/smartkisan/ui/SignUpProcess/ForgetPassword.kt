package com.appdev.smartkisan.ui.SignUpProcess

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.appdev.smartkisan.Actions.UserAuthAction
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.UserAuthState
import com.appdev.smartkisan.ViewModel.LoginViewModel
import com.appdev.smartkisan.ui.OtherComponents.CustomButton
import com.appdev.smartkisan.ui.theme.buttonColor

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
            onAction(UserAuthAction.ModifyToastState(
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


@Composable
fun ResetPasswordConfirmationScreen(
    navigateToLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.tempicon),
                contentDescription = "Email sent icon",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Reset Link Sent!",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "We've sent a password reset link to your email address. Please check your inbox and click on the link to reset your password.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            CustomButton(
                onClick = navigateToLogin,
                text = "Back to Login",
                width = 0.8f
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordRoot(
    loginViewModel: LoginViewModel,
    navigateToLogin: () -> Unit
) {
    ResetPassword(
        loginState = loginViewModel.loginState,
        onAction = { action ->
            when (action) {
                is UserAuthAction.GoBack -> {
                    navigateToLogin()
                }
                else -> loginViewModel.onAction(action)
            }
        },
        navigateToLogin = navigateToLogin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPassword(
    loginState: UserAuthState,
    onAction: (UserAuthAction) -> Unit,
    navigateToLogin: () -> Unit
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

    LaunchedEffect(loginState.passwordResetSuccess) {
        if (loginState.passwordResetSuccess) {
            onAction(UserAuthAction.ModifyToastState(
                Pair(true, "Password reset successful. You can now login with your new password.")
            ))
            navigateToLogin()
        }
    }


    // Prevent access if deep link not verified
    if (!loginState.deepLinkVerified && loginState.resetToken == null) {
        LaunchedEffect(Unit) {
            Toast.makeText(
                context,
                "Invalid access. Please use the reset link sent to your email.",
                Toast.LENGTH_LONG
            ).show()
            navigateToLogin()
            return@LaunchedEffect
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
                    text = "Reset Password",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Create a new password for your account",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // New Password Field
                OutlinedTextField(
                    value = loginState.newPassword,
                    onValueChange = { onAction(UserAuthAction.NewPasswordChange(it)) },
                    label = { Text("New Password") },
                    placeholder = { Text("Enter new password") },
                    visualTransformation = if (loginState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { onAction(UserAuthAction.UpdatePasswordVisible(!loginState.passwordVisible)) }) {
                            Icon(
                                imageVector = if (loginState.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (loginState.passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = buttonColor,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedLabelColor = buttonColor,
                        cursorColor = buttonColor
                    )
                )

                // Confirm New Password Field
                OutlinedTextField(
                    value = loginState.confirmNewPassword,
                    onValueChange = { onAction(UserAuthAction.ConfirmNewPasswordChange(it)) },
                    label = { Text("Confirm New Password") },
                    placeholder = { Text("Confirm new password") },
                    visualTransformation = if (loginState.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { onAction(UserAuthAction.UpdateConfirmPasswordVisible(!loginState.confirmPasswordVisible)) }) {
                            Icon(
                                imageVector = if (loginState.confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (loginState.confirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
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
                    onAction(UserAuthAction.UpdatePassword(loginState.newPassword))
                },
                text = "Reset Password",
                width = 1f
            )
        }

        if (loginState.showToastState.first) {
            Toast.makeText(context, loginState.showToastState.second, Toast.LENGTH_SHORT).show()
            onAction(UserAuthAction.ModifyToastState(Pair(false, "")))
        }
    }
}