package com.appdev.smartkisan.ui.MainAppScreens

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.appdev.smartkisan.Actions.DiseaseDetectActions
import com.appdev.smartkisan.ViewModel.DiseaseDetectViewModel
import com.appdev.smartkisan.ui.OtherComponents.CustomButton
import com.image.cropview.CropType
import com.image.cropview.EdgeType
import com.image.cropview.ImageCrop


@Composable
fun ImageCropperRoot(
    controller: NavHostController,
    diseaseDetectViewModel: DiseaseDetectViewModel
) {
    val state by diseaseDetectViewModel.detectUiState.collectAsStateWithLifecycle()

    // Handle back navigation
    BackHandler {
        diseaseDetectViewModel.onAction(DiseaseDetectActions.ClearData)
        controller.navigateUp()
    }

    // Check if we have an image to crop
    if (state.originalImageBitmap != null) {
        ImageCropScreen(
            bitmap = state.originalImageBitmap!!,
            onCropFinished = { croppedBitmap ->
                diseaseDetectViewModel.onAction(DiseaseDetectActions.FinishCropping(croppedBitmap))
                controller.navigateUp() // Return to previous screen after cropping
            },
            onCancel = {
                diseaseDetectViewModel.onAction(DiseaseDetectActions.ClearData)
                controller.navigateUp()
            }
        )
    } else {
        // Show error or loading state if no image is available
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No image available to crop")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCropScreen(
    bitmap: Bitmap,
    onCropFinished: (Bitmap) -> Unit,
    onCancel: () -> Unit
) {
    // Create a mutable bitmap state that we'll rotate
    var currentBitmap by remember { mutableStateOf(bitmap) }
    // Create the ImageCrop with our current bitmap
    var imageCrop by remember { mutableStateOf(ImageCrop(currentBitmap)) }

    // Function to rotate the bitmap
    fun rotateBitmap(degrees: Float) {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(degrees)
        val rotatedBitmap = Bitmap.createBitmap(
            currentBitmap, 0, 0,
            currentBitmap.width, currentBitmap.height,
            matrix, true
        )
        // Update the bitmap and recreate the ImageCrop instance
        currentBitmap = rotatedBitmap
        imageCrop = ImageCrop(rotatedBitmap)
    }

    // Handle back button press
    BackHandler {
        onCancel()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Crop Image", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { onCancel() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val croppedBitmap = imageCrop.onCrop()
                        onCropFinished(croppedBitmap)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            // ImageCropView with margin
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth().fillMaxHeight(0.5f)
                        .padding(horizontal = 25.dp, vertical = 10.dp) // Added margin from all sides
                ) {
                    imageCrop.ImageCropView(
                        modifier = Modifier.fillMaxSize(),
                        guideLineColor = Color.LightGray,
                        guideLineWidth = 2.dp,
                        edgeCircleSize = 5.dp,
                        showGuideLines = true,
                        cropType = CropType.FREE_STYLE,
                        edgeType = EdgeType.SQUARE
                    )
                }

                CustomButton(onClick = {
                    rotateBitmap(90f)
                }, text = "Rotate Image", width = 1f, modifier = Modifier.padding(horizontal = 20.dp))
            }
        }
    }
}