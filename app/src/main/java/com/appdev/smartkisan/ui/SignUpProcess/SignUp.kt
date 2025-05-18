package com.appdev.smartkisan.ui.SignUpProcess

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.appdev.smartkisan.Actions.UserAuthAction
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.UserAuthState
import com.appdev.smartkisan.ViewModel.LoginViewModel
import com.appdev.smartkisan.ui.OtherComponents.CustomButton
import com.appdev.smartkisan.ui.theme.buttonColor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay

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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SignUp(loginState: UserAuthState, onAction: (UserAuthAction) -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(key1 = loginState.otpRequestAccepted) {
        if (loginState.otpRequestAccepted) {
            onAction(UserAuthAction.NextScreen)
        }
    }

    LaunchedEffect(loginState.validationError) {
        loginState.validationError?.let { error ->
            onAction(UserAuthAction.ModifyToastState(Pair(true, error)))
             onAction(UserAuthAction.ClearValidationError)
        }
    }

    LaunchedEffect(loginState.errorMessage) {
        loginState.errorMessage?.let { error ->
            onAction(UserAuthAction.ModifyToastState(Pair(true, error)))
             onAction(UserAuthAction.ClearValidationError)
        }
    }

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onAction(UserAuthAction.SelectedImageUri(uri))
        }
    }

    val storagePermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(
            permission = Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        rememberPermissionState(
            permission = Manifest.permission.READ_EXTERNAL_STORAGE
        )
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
                                Color.Transparent, RoundedCornerShape(100.dp)
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

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 3.dp, bottom = 4.dp) // Adjust as needed
                    ) {
                        IconButton(
                            onClick = {
                                if (storagePermissionState.status.isGranted) {
                                    launcher.launch("image/*")
                                } else if (!storagePermissionState.status.isGranted &&
                                    !storagePermissionState.status.shouldShowRationale
                                ) {
                                    onAction(UserAuthAction.ShowStoragePermissionDialog)
                                } else {
                                    storagePermissionState.launchPermissionRequest()
                                }
                            },
                            modifier = Modifier
                                .size(30.dp),
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF83E978))
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

                OutlinedTextField(
                    value = loginState.userName,
                    onValueChange = { input ->
                        onAction(UserAuthAction.Username(username = input))
                    },
                    label = { Text("Name") },
                    placeholder = { Text("Enter your name") },
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

                OutlinedTextField(
                    value = loginState.email,
                    onValueChange = { onAction(UserAuthAction.EmailChange(it)) },
                    label = { Text("Email") },
                    placeholder = { Text("Enter email address") },
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

                OutlinedTextField(
                    value = loginState.password,
                    onValueChange = { onAction(UserAuthAction.PasswordChange(it)) },
                    label = { Text("Password") },
                    placeholder = { Text("Enter password") },
                    visualTransformation = if (loginState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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

                OutlinedTextField(
                    value = loginState.confirmPassword,
                    onValueChange = { onAction(UserAuthAction.ConfirmPasswordChange(it)) },
                    label = { Text("Confirm Password") },
                    placeholder = { Text("Confirm password") },
                    visualTransformation = if (loginState.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            onAction(
                                UserAuthAction.UpdateConfirmPasswordVisible(
                                    !loginState.confirmPasswordVisible
                                )
                            )
                        }) {
                            Icon(
                                imageVector = if (loginState.confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (loginState.confirmPasswordVisible) "Hide confirm password" else "Show confirm password"
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
                        text = "Already have an account?",
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Login",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
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

        // Show permission dialog if needed
        if (loginState.showStoragePermissionDialog) {
            AlertDialog(
                onDismissRequest = {
                    onAction(UserAuthAction.DismissStoragePermissionDialog)
                },
                title = {
                    Text(text = "Storage Permission Needed")
                },
                text = {
                    Text(
                        text = "To select a profile picture, this app needs access to your device's storage. " +
                                "Please enable storage permission in the app settings to continue."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onAction(UserAuthAction.DismissStoragePermissionDialog)
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            context.startActivity(intent)
                        },
                    ) {
                        Text("Open Settings")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onAction(UserAuthAction.DismissStoragePermissionDialog)
                        },
                    ) {
                        Text("Not Now")
                    }
                },
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