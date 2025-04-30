package com.appdev.smartkisan.ui.SignUpProcess

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.appdev.smartkisan.Actions.UserAuthAction
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.UserAuthState
import com.appdev.smartkisan.Utils.SessionManagement
import com.appdev.smartkisan.ViewModel.LoginViewModel
import com.appdev.smartkisan.ui.OtherComponents.CustomButton
import com.talhafaki.composablesweettoast.util.SweetToastUtil.SweetError

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
//            is UserAuthAction.ForgotPasswordScreen -> {
//                navigateToForgotPassword()
//            }
            else -> loginViewModel.onAction(action)
        }
    })
}

@Composable
fun Login(loginState: UserAuthState, onAction: (UserAuthAction) -> Unit) {
    val context = LocalContext.current
    var showToastState by remember { mutableStateOf(Pair(false, "")) }

    LaunchedEffect(loginState.errorMessage) {
        loginState.errorMessage?.let { error ->
            showToastState = Pair(true, error)
        }
    }
    LaunchedEffect(key1 = loginState.loginSuccess) {
        if (loginState.loginSuccess) {
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
            SessionManagement.saveUserName(context, userName = loginState.userName, userImage = loginState.imageUrl)
            SessionManagement.saveUserType(
                context,
                userType = loginState.userType
            )
            onAction(UserAuthAction.NextScreen)
        }
    }

    LaunchedEffect(loginState.validationError) {
        loginState.validationError?.let { error ->
            showToastState = Pair(true, error)
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
                        CircularProgressIndicator()
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

                // Email Field
                TextField(
                    value = loginState.email,
                    onValueChange = { onAction(UserAuthAction.EmailChange(it)) },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFE4E7EE),
                        unfocusedContainerColor = Color(0xFFE4E7EE)
                    ),
                    placeholder = { Text("Enter email address") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Password Field
                TextField(
                    value = loginState.password,
                    onValueChange = { onAction(UserAuthAction.PasswordChange(it)) },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFE4E7EE),
                        unfocusedContainerColor = Color(0xFFE4E7EE)
                    ),
                    placeholder = { Text("Enter password") },
                    visualTransformation = if (loginState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { onAction(UserAuthAction.UpdatePasswordVisible(!loginState.passwordVisible)) }) {
                            Image(
                                painter = painterResource(
                                    id = if (loginState.passwordVisible)
                                        R.drawable.show
                                    else
                                        R.drawable.hide
                                ),modifier = Modifier.size(27.dp),
                                contentDescription = if (loginState.passwordVisible) "Hide apassword" else "Show apassword"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = "Forgot Password?",
                    modifier = Modifier
                        .clickable {
//                            onAction(UserAuthAction.ForgotPasswordScreen)
                        }
                        .align(Alignment.End),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
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
                        color = MaterialTheme.colorScheme.primary,
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