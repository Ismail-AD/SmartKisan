package com.appdev.smartkisan.presentation.feature.farmer.account


import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.appdev.smartkisan.R
import com.appdev.smartkisan.presentation.Resuable.NoDialogLoader
import com.appdev.smartkisan.presentation.feature.Seller.account.EditUsernameDialog
import com.appdev.smartkisan.presentation.feature.Seller.account.ImageConfirmationDialog
import com.appdev.smartkisan.presentation.feature.Seller.account.LogoutConfirmDialog
import com.appdev.smartkisan.presentation.feature.Seller.account.OptionMenu
import com.appdev.smartkisan.presentation.feature.Seller.account.UpdatePasswordDialog
import com.appdev.smartkisan.presentation.navigation.Routes
import com.appdev.smartkisan.presentation.theme.myGreen

@Composable
fun UserAccountRoot(
    controller: NavHostController,
    viewModel: UserProfileViewModel = hiltViewModel(),
    navigateToAuth: () -> Unit
) {
    val state by viewModel.userProfileState.collectAsStateWithLifecycle()

    // Image picker launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onEvent(UserProfileActions.ShowImageConfirmationDialog(it))
        }
    }

    LaunchedEffect(key1 = state.isLogoutConfirmed) {
        if (state.isLogoutConfirmed) {
            navigateToAuth()
        }
    }

    UserProfileScreen(
        state = state,
        onProfileAction = { action ->
            when (action) {
                is UserProfileActions.LaunchImagePicker -> {
                    imagePicker.launch("image/*")
                }

                is UserProfileActions.GoToChats -> {
                    controller.navigate(Routes.UserChatListScreen.route)
                }

                is UserProfileActions.GoToSavedProducts -> {
                    // Navigate to saved products screen if needed
                }

                else -> viewModel.onEvent(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    state: UserProfileState,
    onProfileAction: (UserProfileActions) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            onProfileAction.invoke(UserProfileActions.ClearError)
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            onProfileAction.invoke(UserProfileActions.DismissMessage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading && !state.showEditUsernameDialog && !state.showUpdatePasswordDialog && !state.showImageConfirmationDialog) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                NoDialogLoader("Loading Profile...")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Image with Edit Icon
                Box(
                    modifier = Modifier
                        .size(120.dp)
                ) {
                    // Profile Image
                    Surface(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(BorderStroke(2.dp, myGreen), CircleShape)
                            .clickable { onProfileAction(UserProfileActions.LaunchImagePicker) },
                        shape = CircleShape
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(state.userEntity.imageUrl)
                                .build(),
                            contentDescription = "Profile Image",
                            placeholder = painterResource(R.drawable.farmer),
                            error = painterResource(R.drawable.farmer),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Box(modifier = Modifier
                        .align(Alignment.BottomEnd).padding(end = 6.dp)) {
                        IconButton(
                            onClick = { onProfileAction(UserProfileActions.LaunchImagePicker) },
                            modifier = Modifier
                                .size(35.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = myGreen           // circular background
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile Picture",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                // Username
                Text(
                    text = state.userEntity.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                // Email
                Text(
                    text = state.userEntity.email,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(modifier = Modifier.height(16.dp))

                // Options Menu
                Text(
                    text = "Account Settings",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OptionMenu(
                    icon = Icons.Default.Person,
                    title = "Update Username",
                    onClick = {
                        onProfileAction(UserProfileActions.ShowEditUsernameDialog)
                    }
                )

                OptionMenu(
                    icon = Icons.Default.Password,
                    title = "Update Password",
                    onClick = {
                        onProfileAction(UserProfileActions.ShowUpdatePasswordDialog)
                    }
                )

                OptionMenu(
                    icon = Icons.Default.Chat,
                    title = "Chat",
                    onClick = {
                        onProfileAction(UserProfileActions.GoToChats)
                    }
                )

                OptionMenu(
                    icon = Icons.Default.Logout,
                    title = "Logout",
                    onClick = {
                        onProfileAction(UserProfileActions.ShowLogoutConfirmDialog)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Show dialogs based on state
    if (state.showEditUsernameDialog) {
        EditUsernameDialog(
            oldUsername = state.userEntity.name,
            username = state.temporaryUsername,
            onUsernameChange = { username ->
                onProfileAction(UserProfileActions.SetTemporaryUsername(username))
            },
            onSave = {
                onProfileAction(UserProfileActions.UpdateUsername(state.temporaryUsername))
            },
            onDismiss = {
                onProfileAction(UserProfileActions.DismissAllDialogs)
            },
            error = state.usernameError,
            isLoading = state.isLoading
        )
    }

    if (state.showImageConfirmationDialog && state.selectedImageUri != null) {
        ImageConfirmationDialog(
            imageUri = state.selectedImageUri,
            onConfirm = {
                onProfileAction(UserProfileActions.UpdateProfileImage(state.selectedImageUri))
            },
            onDismiss = {
                onProfileAction(UserProfileActions.DismissImageConfirmationDialog)
            },
            isLoading = state.isLoading
        )
    }

    if (state.showUpdatePasswordDialog) {
        UpdatePasswordDialog(
            password = state.temporaryPassword,
            confirmPassword = state.temporaryConfirmPassword,
            onPasswordChange = { password ->
                onProfileAction(UserProfileActions.SetTemporaryPassword(password))
            },
            onConfirmPasswordChange = { confirmPassword ->
                onProfileAction(UserProfileActions.SetTemporaryConfirmPassword(confirmPassword))
            },
            onSave = {
                onProfileAction(
                    UserProfileActions.UpdatePassword(
                        state.temporaryCurrentPassword,
                        state.temporaryPassword,
                        state.temporaryConfirmPassword
                    )
                )
            },
            onDismiss = {
                onProfileAction(UserProfileActions.DismissAllDialogs)
            },
            newPasswordError = state.newPasswordError,
            currentPasswordError = state.currentPasswordError,
            confirmPasswordError = state.confirmPasswordError,
            currentPassword = state.temporaryCurrentPassword,
            onCurrentPasswordChange = { currPassword ->
                onProfileAction(UserProfileActions.SetTemporaryCurrentPassword(currPassword))
            },
            isLoading = state.isLoading
        )
    }

    if (state.showLogoutConfirmDialog) {
        LogoutConfirmDialog(
            isLoading = state.isLoading,
            onDismiss = { onProfileAction(UserProfileActions.HideLogoutConfirmDialog) },
            onConfirm = { onProfileAction(UserProfileActions.Logout) }
        )
    }
}
