package com.appdev.smartkisan.ui.SignUpProcess

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.appdev.smartkisan.Actions.UserAuthAction
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.UserAuthState
import com.appdev.smartkisan.Utils.SessionManagement
import com.appdev.smartkisan.ViewModel.LoginViewModel
import com.appdev.smartkisan.ui.OtherComponents.CustomButton
import com.talhafaki.composablesweettoast.util.SweetToastUtil.SweetError
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SignUpRoot(
    navigateToNext: () -> Unit,
    loginViewModel: LoginViewModel,
    navigateToLogin: () -> Unit,
    navigateUp: () -> Unit
) {
    SignUp(loginViewModel.loginState, onAction = { action ->
        when (action) {
            is UserAuthAction.NextScreen -> {
                navigateToNext()
            }

            is UserAuthAction.GoBack -> {
                navigateUp()
            }

            is UserAuthAction.LoginScreen -> {
                navigateToLogin()
            }

            else -> loginViewModel.onAction(action)
        }
    })
}

@Composable
fun SignUp(loginState: UserAuthState, onAction: (UserAuthAction) -> Unit) {

    val context = LocalContext.current


    var showToastState by remember { mutableStateOf(Pair(false, "")) }

    LaunchedEffect(key1 = loginState.otpRequestAccepted) {
        if (loginState.otpRequestAccepted) {
            onAction(UserAuthAction.NextScreen)
        }
    }
    LaunchedEffect(loginState.validationError) {
        loginState.validationError?.let { error ->
            showToastState = Pair(true, error)
        }
    }
    LaunchedEffect(loginState.errorMessage) {
        loginState.errorMessage?.let { error ->
            showToastState = Pair(true, error)
        }
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onAction.invoke(UserAuthAction.SelectedImageUri(uri))
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
//            Image(painter = painterResource(id = R.drawable.tempicon), contentDescription = "")

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Account",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Please fill in the details to create your account",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Box(modifier = Modifier.padding(top = 15.dp)) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFF9933), Color(0xFFFF5533))
                                ), RoundedCornerShape(100.dp)
                            )
                            .clip(CircleShape)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(loginState.profileImage)
                                .build(),
                            contentDescription = "Profile Image",
                            placeholder = painterResource(R.drawable.farmer),
                            error = painterResource(R.drawable.farmer),
                            modifier = Modifier
                                .size(120.dp)
                                .padding(),
                            contentScale = ContentScale.Crop
                        )
                    }


                    IconButton(
                        onClick = {
                            launcher.launch("image/*")
                        }, modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(40.dp)
                    ) {
                        Card(
                            shape = CircleShape, colors = CardDefaults.cardColors(
                                containerColor = Color(
                                    0xFF83E978
                                )
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "",
                                tint = Color.Black.copy(alpha = 0.9f),
                                modifier = Modifier.size(27.dp)
                            )
                        }
                    }
                }
                TextField(
                    value = loginState.userName,
                    onValueChange = { input ->
                        onAction.invoke(UserAuthAction.Username(username = input))
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFE4E7EE),
                        unfocusedContainerColor = Color(0xFFE4E7EE),
                    ),
                    placeholder = {
                        Text(
                            text = "Enter your name"
                        )
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp), singleLine = true
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
                                ), modifier = Modifier.size(27.dp),
                                contentDescription = if (loginState.passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Confirm Password Field
                TextField(
                    value = loginState.confirmPassword,
                    onValueChange = { onAction(UserAuthAction.ConfirmPasswordChange(it)) },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFE4E7EE),
                        unfocusedContainerColor = Color(0xFFE4E7EE)
                    ),
                    placeholder = { Text("Confirm password") },
                    visualTransformation = if (loginState.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = {
                            onAction(
                                UserAuthAction.UpdateConfirmPasswordVisible(
                                    !loginState.confirmPasswordVisible
                                )
                            )
                        }) {
                            Image(
                                painter = painterResource(
                                    id = if (loginState.confirmPasswordVisible)
                                        R.drawable.show
                                    else
                                        R.drawable.hide
                                ), modifier = Modifier.size(27.dp),
                                contentDescription = if (loginState.confirmPasswordVisible) "Hide cpassword" else "Show cpassword"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Already have an account?",
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Login",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onAction(UserAuthAction.LoginScreen) }
                    )
                }
            }

            CustomButton(
                onClick = {
                    onAction(
                        UserAuthAction.SendMeOtp(
                            loginState.email,
                            loginState.password
                        )
                    )
                },
                text = "Sign Up",
                width = 1f
            )
        }
        if (showToastState.first) {
            Toast.makeText(context, showToastState.second, Toast.LENGTH_SHORT).show()
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




