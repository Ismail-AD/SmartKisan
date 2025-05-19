package com.appdev.smartkisan.presentation.feature.auth.login

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
import com.appdev.smartkisan.R
import com.appdev.smartkisan.presentation.Resuable.CustomButton
import com.appdev.smartkisan.presentation.theme.buttonColor

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun LoginRoot(
    loginViewModel: LoginViewModel,
    navigateToSignUp: () -> Unit,
    navigateUp: () -> Unit,
    navigateToNext: () -> Unit,
    navigateToForgotPassword: () -> Unit
) {
    Login(loginViewModel.loginState, onAction = { action ->
        when (action) {
            is UserAuthAction.NextScreen -> {
                navigateToNext()
            }

            is UserAuthAction.SignUpScreen -> {
                navigateToSignUp()
            }

            is UserAuthAction.GoBack -> {
                navigateUp()
            }
            is UserAuthAction.ForgotPasswordScreen -> {
                navigateToForgotPassword()
            }
            else -> loginViewModel.onAction(action)
        }
    })
}

@Composable
fun Login(loginState: UserAuthState, onAction: (UserAuthAction) -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(loginState.errorMessage) {
        loginState.errorMessage?.let { error ->
            onAction(UserAuthAction.ModifyToastState(Pair(true, error)))
            onAction(UserAuthAction.ClearValidationError)
        }
    }

    LaunchedEffect(key1 = loginState.loginSuccess) {
        if (loginState.loginSuccess) {
            onAction(UserAuthAction.NextScreen)
        }
    }

    LaunchedEffect(loginState.validationError) {
        loginState.validationError?.let { error ->
            onAction(UserAuthAction.ModifyToastState(Pair(true, error)))
            onAction(UserAuthAction.ClearValidationError)
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
                    text = "Welcome Back",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Login to your account to continue",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Email Field - Updated to OutlinedTextField with SignUp screen styling
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

                // Password Field - Updated to OutlinedTextField with SignUp screen styling
                OutlinedTextField(
                    value = loginState.password,
                    onValueChange = { onAction(UserAuthAction.PasswordChange(it)) },
                    label = { Text("Password") },
                    placeholder = { Text("Enter password") },
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

                Text(
                    text = "Forgot Password?",
                    modifier = Modifier
                        .clickable {
                            onAction(UserAuthAction.ForgotPasswordScreen)
                        }
                        .align(Alignment.End),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.surfaceContainerLow
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Don't have an account?",
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Sign Up",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.clickable { onAction(UserAuthAction.SignUpScreen) }
                    )
                }
            }

            CustomButton(
                onClick = {
                    onAction(
                        UserAuthAction.LoginUser(
                            loginState.email,
                            loginState.password
                        )
                    )
                },
                text = "Login",
                width = 1f
            )
        }

        if (loginState.showToastState.first) {
            Toast.makeText(context, loginState.showToastState.second, Toast.LENGTH_SHORT).show()
            onAction(
                UserAuthAction.ModifyToastState(
                    Pair(false, "")
                )
            )
        }
    }
}