package com.appdev.smartkisan.ui.MainAppScreens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.appdev.smartkisan.Actions.DiseaseDetectActions
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.DiseaseDetectState
import com.appdev.smartkisan.States.PlantItem
import com.appdev.smartkisan.ViewModel.DiseaseDetectViewModel
import com.appdev.smartkisan.ui.OtherComponents.CustomButton
import com.appdev.smartkisan.ui.OtherComponents.CustomLoader
import com.appdev.smartkisan.ui.navigation.Routes
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PlantDiseaseRoot(
    controller: NavHostController,
    diseaseDetectViewModel: DiseaseDetectViewModel = hiltViewModel()
) {
    val state by diseaseDetectViewModel.detectUiState.collectAsStateWithLifecycle()
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val context = LocalContext.current

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            // Start cropping flow with captured image
            diseaseDetectViewModel.onAction(DiseaseDetectActions.CaptureOriginalImage(bitmap))
        }
    }


    PlantDisease(state) { action ->
        when (action) {
            is DiseaseDetectActions.GoBack -> {
                controller.navigateUp()
            }

            is DiseaseDetectActions.OpenCamera -> {
                if (permissionState.status.isGranted) {
                    cameraLauncher.launch(null)
                } else if (!permissionState.status.isGranted &&
                    !permissionState.status.shouldShowRationale
                ) {
                    diseaseDetectViewModel.onAction(DiseaseDetectActions.PermissionDeniedPermanent)
                } else {
                    permissionState.launchPermissionRequest()
                }
            }

            is DiseaseDetectActions.NavigateToResultScreen -> {
                Log.d("AWQZWR", "GOING")
                controller.navigate(Routes.DiagnosisResult.route)
            }

            else -> diseaseDetectViewModel.onAction(action)
        }
    }
    LaunchedEffect(state.showCropper, state.originalImageBitmap) {
        if (state.showCropper && state.originalImageBitmap != null) {
            controller.navigate(Routes.ImageCropper.route)
        }
    }
    if (state.showCameraPermitRationale) {
        AlertDialog(
            onDismissRequest = {
                diseaseDetectViewModel.onAction(DiseaseDetectActions.DismissCameraDialog)
            },
            title = {
                Text(text = "Camera Permission Needed")
            },
            text = {
                Text(
                    text = "To diagnose plant diseases using your camera, this app needs access to your device's camera. " +
                            "Please enable camera permission in the app settings to continue."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        diseaseDetectViewModel.onAction(DiseaseDetectActions.DismissCameraDialog)
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        context.startActivity(intent)
                    },
                ) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        diseaseDetectViewModel.onAction(DiseaseDetectActions.DismissCameraDialog)
                    },
                ) {
                    Text("Dismiss")
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PlantDisease(detectUiState: DiseaseDetectState, onAction: (DiseaseDetectActions) -> Unit) {
    val context = LocalContext.current

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onAction(DiseaseDetectActions.AddSelectedImage(uri))

            // Get bitmap from URI and start cropping flow
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                val inputStream = context.contentResolver.openInputStream(uri)
                val drawable = android.graphics.drawable.Drawable.createFromStream(
                    inputStream,
                    uri.toString()
                )
                inputStream?.close()
                drawable?.toBitmap()
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }

            bitmap?.let {
                onAction(DiseaseDetectActions.StartCropping(it))
            }
        }
    }

    LaunchedEffect(detectUiState.diseaseDetails) {
        Log.d("AWQZWR", "${detectUiState.diseaseDetails}")
        if (detectUiState.diseaseDetails != null && detectUiState.diseaseDetails.confidence > 0f) {
            Log.d("AWQZWR", "GOING")
            onAction(DiseaseDetectActions.NavigateToResultScreen)
        }
    }

    LaunchedEffect(detectUiState.error) {
        detectUiState.error?.let { error ->
            Log.d("myerror", error)
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            onAction(DiseaseDetectActions.ClearValidationError)
        }
    }

    LaunchedEffect(detectUiState.diagnosisResult) {
        detectUiState.diagnosisResult?.let { result ->
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = "Disease Detection", fontSize = 19.sp, fontWeight = FontWeight.Bold)
            },
            actions = {
                IconButton(onClick = {
                    onAction(DiseaseDetectActions.ToggleInstructionDialog)
                }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Show Instructions",
                        tint = Color(0xFF2E7D32)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
    }) { paddings ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            if (detectUiState.isLoading || detectUiState.processingImage) {
                CustomLoader()
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = paddings
            ) {
                // Clickable tabs
                item {
                    ClickableTabs(
                        selectedItem = detectUiState.currentTab,
                        tabsList = listOf("Crops", "Fruits"),
                        onClick = { tabIndex ->
                            onAction(DiseaseDetectActions.CurrentSelectedTab(tabIndex))
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Plant items based on selected tab
                item {
                    when (detectUiState.currentTab) {
                        0 -> { // Crops
                            PlantItemsList(
                                items = detectUiState.cropItems,
                                selectedItem = detectUiState.selectedPlantItem,
                                onItemClick = { itemId ->
                                    onAction(DiseaseDetectActions.SelectPlantItem(itemId))
                                }
                            )
                        }

                        1 -> { // Fruits
                            PlantItemsList(
                                items = detectUiState.fruitItems,
                                selectedItem = detectUiState.selectedPlantItem,
                                onItemClick = { itemId ->
                                    onAction(DiseaseDetectActions.SelectPlantItem(itemId))
                                }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    Card(
                        border = BorderStroke(
                            width = 2.dp,
                            color = Color(0xFF2E7D32)
                        )
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(
                                    detectUiState.selectedImageBitmap
                                        ?: detectUiState.selectedImageUri
                                )
                                .build(),
                            placeholder = painterResource(R.drawable.placholder),
                            error = painterResource(R.drawable.placholder),
                            contentDescription = "",
                            modifier = Modifier.size(200.dp),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    ImageActionButtons(
                        onCameraClick = {
                            onAction(DiseaseDetectActions.OpenCamera)
                        },
                        onGalleryClick = {
                            galleryLauncher.launch("image/*")
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    CustomButton(onClick = {
                        if (detectUiState.selectedPlantItem == null) {
                            onAction(DiseaseDetectActions.ClearValidationError) // Clear any existing error first
                            onAction(DiseaseDetectActions.ValidationError("Please select a plant type first"))
                        } else if (detectUiState.selectedImageBitmap == null) {
                            onAction(DiseaseDetectActions.ClearValidationError) // Clear any existing error first
                            onAction(DiseaseDetectActions.ValidationError("Please select or capture an image first"))
                        } else {
                            onAction(DiseaseDetectActions.StartDiagnosis(context))
                        }
                    }, text = "Diagnose", width = 1f)
                }
            }

            // Instruction Dialog
            if (detectUiState.showInstructionDialog) {
                AlertDialog(
                    onDismissRequest = {
                        onAction(DiseaseDetectActions.ToggleInstructionDialog)
                    },
                    title = {
                        Text(text = "Instructions", fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Text(
                            text = "Make sure the whole leaf is clear and looks like the sample shown and put the leaf on a plain background for better results.",
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onAction(DiseaseDetectActions.ToggleInstructionDialog)
                            }
                        ) {
                            Text("OK", color = Color(0xFF2E7D32))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PlantItemsList(
    items: List<PlantItem>,
    selectedItem: String?,
    onItemClick: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            PlantItemCard(
                item = item,
                isSelected = item.plantName == selectedItem,
                onClick = { onItemClick(item.plantName) }
            )
        }
    }
}


@Composable
fun PlantItemCard(
    item: PlantItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF2E7D32) else Color.Transparent
    val borderWidth = if (isSelected) 2.dp else 0.dp
    val elevationValue = if (isSelected) 8.dp else 2.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevationValue
            ),
            shape = CircleShape,
            modifier = Modifier.size(60.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        BorderStroke(borderWidth, borderColor),
                        CircleShape
                    )
                    .padding(2.dp)
                    .clip(CircleShape)
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.drawable)
                        .build(),
                    contentDescription = item.plantName,
                    modifier = Modifier.fillMaxSize(0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.plantName,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun ImageActionButtons(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Camera Button
        Card(
            onClick = { onCameraClick() },
            colors = CardDefaults.cardColors(containerColor = Color(0xff8ad167)),
            modifier = Modifier
                .weight(1f)
                .height(90.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF66BB6A), Color(0xFF2E7D32))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.camshot),
                        contentDescription = "",
                        modifier = Modifier.size(37.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Take picture",
                        fontSize = 16.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Gallery Button
        Card(
            onClick = { onGalleryClick() },
            colors = CardDefaults.cardColors(containerColor = Color(0xff8ad167)),
            modifier = Modifier
                .weight(1f)
                .height(90.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF66BB6A), Color(0xFF2E7D32))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.importgallery),
                        contentDescription = "",
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Import picture",
                        fontSize = 16.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}