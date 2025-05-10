package com.appdev.smartkisan.ui.SellerAppScreens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.location.LocationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.appdev.smartkisan.Actions.SellerProfileActions
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.LocationPermissionState
import com.appdev.smartkisan.ViewModel.SellerProfileViewModel
import com.appdev.smartkisan.ui.theme.myGreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.appdev.smartkisan.States.SellerProfileState
import com.appdev.smartkisan.ui.OtherComponents.NoDialogLoader
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SellerProfileRoot(
    viewModel: SellerProfileViewModel = hiltViewModel(),
    navigateToAuth: () -> Unit
) {
    val state by viewModel.sellerProfileState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Image picker launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onEvent(SellerProfileActions.ShowImageConfirmationDialog(it))
        }
    }

    // Location settings launcher
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        checkLocationEnabled(context) { isEnabled ->
            if (isEnabled) {
                viewModel.onEvent(
                    SellerProfileActions.UpdateLocationPermission(
                        LocationPermissionState.GRANTED
                    )
                )
                viewModel.onEvent(SellerProfileActions.ShowLocationPicker)
            }
        }
    }

    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(key1 = state.isLogoutConfirmed) {
        if (state.isLogoutConfirmed) {
            navigateToAuth()
        }
    }



    SellerProfileScreen(
        state = state,
        onProfileAction = { action ->
            when (action) {
                is SellerProfileActions.LaunchImagePicker -> {
                    imagePicker.launch("image/*")
                }

                is SellerProfileActions.LaunchLocationSettings -> {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    locationSettingsLauncher.launch(intent)
                }

                is SellerProfileActions.ShowLocationPicker -> {
                    // Check if we already have valid coordinates
                    if (state.seller.latitude != 0.0 && state.seller.longitude != 0.0) {
                        // Directly open the location picker map
                        viewModel.onEvent(SellerProfileActions.ShowLocationPicker)
                    } else {
                        // Need to check permissions and location settings first
                        if (locationPermissionState.status.isGranted) {
                            // Permission granted, check if location is enabled
                            val locationManager =
                                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            val isLocationEnabled =
                                LocationManagerCompat.isLocationEnabled(locationManager)

                            if (isLocationEnabled) {
                                viewModel.onEvent(
                                    SellerProfileActions.UpdateLocationPermission(
                                        LocationPermissionState.GRANTED
                                    )
                                )
                                viewModel.onEvent(SellerProfileActions.ShowLocationPicker)
                            } else {
                                viewModel.onEvent(
                                    SellerProfileActions.UpdateLocationPermission(
                                        LocationPermissionState.LOCATION_DISABLED
                                    )
                                )
                            }
                        } else if (!locationPermissionState.status.isGranted &&
                            !locationPermissionState.status.shouldShowRationale
                        ) {
                            // Permission permanently denied
                            viewModel.onEvent(
                                SellerProfileActions.UpdateLocationPermission(
                                    LocationPermissionState.PERMANENTLY_DENIED
                                )
                            )
                        } else {
                            // Request location permission
                            locationPermissionState.launchPermissionRequest()
                        }
                    }
                }

                else -> viewModel.onEvent(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerProfileScreen(
    state: SellerProfileState,
    onProfileAction: (SellerProfileActions) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            onProfileAction.invoke(SellerProfileActions.ClearError)
        }
    }
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            onProfileAction.invoke(SellerProfileActions.ClearError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seller Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        if (!state.showLocationPickerMap && state.isLoading && !state.showEditContactDialog && !state.showEditUsernameDialog && !state.showEditShopNameDialog && !state.showImageConfirmationDialog) {
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
                            .clickable { onProfileAction(SellerProfileActions.LaunchImagePicker) },
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

                    // Edit Icon
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.BottomEnd)
                            .clickable { onProfileAction(SellerProfileActions.LaunchImagePicker) },
                        shape = CircleShape,
                        color = myGreen
                    ) {
                        Box(contentAlignment = Alignment.Center) {
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

                // Shop Name
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
                    title = "Edit Username",
                    onClick = {
                        onProfileAction(SellerProfileActions.ShowEditUsernameDialog)
                    }
                )

                OptionMenu(
                    icon = Icons.Default.Phone,
                    title = "Update Contact Number",
                    onClick = {
                        onProfileAction(SellerProfileActions.ShowEditContactDialog)
                    }
                )

                OptionMenu(
                    icon = Icons.Default.Inventory2,
                    title = "Edit Shop Name",
                    onClick = {
                        onProfileAction(SellerProfileActions.ShowEditShopNameDialog)
                    }
                )

                OptionMenu(
                    icon = Icons.Default.Password,
                    title = "Update Password",
                    onClick = {
                        onProfileAction(SellerProfileActions.ShowUpdatePasswordDialog)
                    }
                )

                OptionMenu(
                    icon = Icons.Default.EditLocationAlt,
                    title = "Update Shop Location",
                    onClick = {
                        onProfileAction(SellerProfileActions.ShowLocationPicker)
                    }
                )
                OptionMenu(
                    icon = Icons.Default.Logout,
                    title = "Logout",
                    onClick = {
                        onProfileAction(SellerProfileActions.ShowLogoutConfirmDialog)
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
                onProfileAction(SellerProfileActions.SetTemporaryUsername(username))
            },
            onSave = {
                onProfileAction(SellerProfileActions.UpdateUsername(state.temporaryUsername))
            },
            onDismiss = {
                onProfileAction(SellerProfileActions.DismissAllDialogs)
            },
            error = state.usernameError, isLoading = state.isLoading
        )
    }

    if (state.showEditShopNameDialog) {
        EditShopNameDialog(
            oldShopName = state.seller.shopName,
            shopName = state.temporaryShopName,
            onShopNameChange = { shopName ->
                onProfileAction(SellerProfileActions.SetTemporaryShopName(shopName))
            },
            onSave = {
                onProfileAction(SellerProfileActions.UpdateShopName(state.temporaryShopName))
            },
            onDismiss = {
                onProfileAction(SellerProfileActions.DismissAllDialogs)
            },
            error = state.shopNameError, isLoading = state.isLoading
        )
    }

    if (state.showImageConfirmationDialog && state.selectedImageUri != null) {
        ImageConfirmationDialog(
            imageUri = state.selectedImageUri,
            onConfirm = {
                // When confirmed, update the profile image
                onProfileAction(SellerProfileActions.UpdateProfileImage(state.selectedImageUri))
            },
            onDismiss = {
                onProfileAction(SellerProfileActions.DismissImageConfirmationDialog)
            },
            isLoading = state.isLoading
        )
    }

    if (state.showEditContactDialog) {
        EditContactDialog(
            oldContact = state.seller.contact,
            contact = state.temporaryContact,
            onContactChange = { contact ->
                onProfileAction(SellerProfileActions.SetTemporaryContact(contact))
            },
            onSave = {
                onProfileAction(SellerProfileActions.UpdateContact(state.temporaryContact))
            },
            onDismiss = {
                onProfileAction(SellerProfileActions.DismissAllDialogs)
            },
            error = state.contactError, isLoading = state.isLoading
        )
    }

    if (state.showUpdatePasswordDialog) {
        // For password dialog, we could have multiple errors - for current implementation we'll use the general error
        // In a more comprehensive solution, you would add specific error fields to the state
        UpdatePasswordDialog(
            password = state.temporaryPassword,
            confirmPassword = state.temporaryConfirmPassword,
            onPasswordChange = { password ->
                onProfileAction(SellerProfileActions.SetTemporaryPassword(password))
            },
            onConfirmPasswordChange = { confirmPassword ->
                onProfileAction(SellerProfileActions.SetTemporaryConfirmPassword(confirmPassword))
            },
            onSave = {
                onProfileAction(
                    SellerProfileActions.UpdatePassword(
                        state.temporaryCurrentPassword,
                        state.temporaryPassword,
                        state.temporaryConfirmPassword
                    )
                )
            },
            onDismiss = {
                onProfileAction(SellerProfileActions.DismissAllDialogs)
            },
            newPasswordError = state.newPasswordError,
            currentPasswordError = state.currentPasswordError,
            confirmPasswordError = state.confirmPasswordError,
            currentPassword = state.temporaryCurrentPassword,
            onCurrentPasswordChange = { currPassword ->
                onProfileAction(SellerProfileActions.SetTemporaryCurrentPassword(currPassword))
            }, isLoading = state.isLoading
        )
    }

    // Keep other dialogs unchanged
    if (state.showLocationDialog) {
        LocationPermissionDialog(
            state = state.locationPermissionState,
            onDismiss = {
                onProfileAction(SellerProfileActions.DismissLocationDialog)
            },
            onOpenSettings = {
                onProfileAction(SellerProfileActions.LaunchLocationSettings)
            }
        )
    }
    if (state.showLogoutConfirmDialog) {
        LogoutConfirmDialog(
            isLoading = state.isLoading,
            onDismiss = { onProfileAction(SellerProfileActions.HideLogoutConfirmDialog) },
            onConfirm = { onProfileAction(SellerProfileActions.Logout) }
        )
    }

    if (state.showLocationPickerMap) {
        LocationPickerDialog(
            isLoading = state.isLoading,
            initialLat = state.seller.latitude,
            initialLong = state.seller.longitude,
            onLocationSelected = { lat, long ->
                onProfileAction(SellerProfileActions.UpdateShopLocation(lat, long))
            },
            onDismiss = {
                onProfileAction(SellerProfileActions.DismissAllDialogs)
            }
        )
    }
}


@Composable
fun LocationPickerDialog(
    isLoading: Boolean = false,
    initialLat: Double,
    initialLong: Double,
    onLocationSelected: (Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    // Use the initial coordinates if available, otherwise use a default location
    val defaultLatLng = LatLng(-34.0, 151.0) // Default coordinates (can be changed)

    val initialLatLng = if (initialLat != 0.0 && initialLong != 0.0) {
        LatLng(initialLat, initialLong)
    } else {
        defaultLatLng
    }

    val markerPosition = remember {
        mutableStateOf(initialLatLng)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition.value, 15f)
    }

    val markerState = remember {
        MarkerState(position = markerPosition.value)
    }

    // Update camera position when marker position changes
    LaunchedEffect(markerPosition.value) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(markerPosition.value, 15f)
    }

    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = true,
            compassEnabled = true
        )
    }

    val properties = remember {
        MapProperties(
            isMyLocationEnabled = true
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Shop Location") },
        text = {
            Column {
                Text(
                    text = "Long press on the map to place marker at your shop location",
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = uiSettings,
                        properties = properties,
                        onMapLongClick = { latLng ->
                            markerPosition.value = latLng
                            markerState.position = latLng
                        }
                    ) {
                        Marker(
                            state = markerState,
                            title = "Shop Location",
                            snippet = "Your shop will be displayed here"
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val latLng = markerState.position
                    onLocationSelected(latLng.latitude, latLng.longitude)
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Confirm Location")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Helper function to check if location services are enabled
private fun checkLocationEnabled(
    context: Context,
    onResult: (Boolean) -> Unit
) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isLocationEnabled = LocationManagerCompat.isLocationEnabled(locationManager)
    onResult(isLocationEnabled)
}


@Composable
fun ImageConfirmationDialog(
    imageUri: Uri?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean
) {
    if (imageUri == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Profile Image") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUri)
                            .build(),
                        contentDescription = "Selected Profile Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Do you want to upload this image as your profile picture?")
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.width(100.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Upload")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun EditUsernameDialog(
    oldUsername: String,
    username: String,
    onUsernameChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    error: String? = null,
    isLoading: Boolean = false
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Edit Username") },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null, // Show error state if there's an error
                    supportingText = {
                        error?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = username.isNotBlank() && oldUsername.trim() != username.trim()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// 2. Update the EditShopNameDialog to show errors in the field

@Composable
fun EditShopNameDialog(
    oldShopName: String,
    shopName: String,
    onShopNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    error: String? = null,
    isLoading: Boolean = false

) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Edit Shop Name") },
        text = {
            Column {
                OutlinedTextField(
                    value = shopName,
                    onValueChange = onShopNameChange,
                    label = { Text("Shop Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null, // Show error state if there's an error
                    supportingText = {
                        error?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = shopName.isNotBlank() && oldShopName.trim() != shopName.trim()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// 3. Update the EditContactDialog to show errors in the field

@Composable
fun EditContactDialog(
    oldContact: String,
    contact: String,
    onContactChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    error: String? = null, isLoading: Boolean = false

) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Edit Contact Number") },
        text = {
            Column {
                OutlinedTextField(
                    value = contact,
                    onValueChange = onContactChange,
                    label = { Text("Phone Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    isError = error != null, // Show error state if there's an error
                    supportingText = {
                        error?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = contact.isNotBlank() && oldContact.trim() != contact.trim()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// 4. Update the UpdatePasswordDialog to show errors in the fields

@Composable
fun UpdatePasswordDialog(
    currentPassword: String,
    password: String,
    confirmPassword: String,
    onCurrentPasswordChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    currentPasswordError: String? = null, // Separate error for current password
    newPasswordError: String? = null, // Separate error for new password
    confirmPasswordError: String? = null,
    isLoading: Boolean = false

) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Update Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = onCurrentPasswordChange,
                    label = { Text("Current Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    isError = currentPasswordError != null,
                    supportingText = {
                        currentPasswordError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    isError = newPasswordError != null,
                    supportingText = {
                        newPasswordError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmPasswordError != null,
                    supportingText = {
                        confirmPasswordError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = currentPassword.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Update")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionDialog(
    state: LocationPermissionState,
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current

    when (state) {
        LocationPermissionState.PERMANENTLY_DENIED -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Location Permission Required") },
                text = {
                    Text(
                        "To provide shop location information, we need access to your location. " +
                                "Please grant location permission in app settings."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDismiss()
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            context.startActivity(intent)
                        }
                    ) {
                        Text("Open Settings")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            )
        }

        LocationPermissionState.LOCATION_DISABLED -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Location Services Disabled") },
                text = {
                    Text(
                        "To select your shop location, please enable location services."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDismiss()
                            onOpenSettings()
                        }
                    ) {
                        Text("Open Location Settings")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            )
        }

        LocationPermissionState.UNKNOWN -> {
            val locationPermissionState = rememberPermissionState(
                permission = Manifest.permission.ACCESS_FINE_LOCATION
            )

            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Location Permission Required") },
                text = {
                    Text(
                        "To select your shop location, we need access to your location. " +
                                "Please grant location permission."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDismiss()
                            locationPermissionState.launchPermissionRequest()
                        }
                    ) {
                        Text("Grant Permission")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            )
        }

        else -> { /* No dialog needed */
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionMenu(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Column {
        Card(
            onClick = onClick,
            modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 15.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Option Icon",
                    modifier = Modifier.size(25.dp),
                    tint = myGreen
                )

                Text(
                    text = title,
                    color = Color.Gray.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 13.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 3.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForwardIos,
                        contentDescription = "Forward Arrow",
                        tint = MaterialTheme.colorScheme.surfaceTint.copy(
                            alpha = 0.6f
                        ),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun LogoutConfirmDialog(
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Logout") },
        text = { Text("Are you sure you want to log out from the application?") },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {

                    Text("Yes, Logout")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}



