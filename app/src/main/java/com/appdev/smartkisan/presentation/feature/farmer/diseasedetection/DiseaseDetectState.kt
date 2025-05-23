package com.appdev.smartkisan.presentation.feature.farmer.diseasedetection

import android.graphics.Bitmap
import android.net.Uri
import com.appdev.smartkisan.R
import com.appdev.smartkisan.domain.model.DiseaseResult

data class DiseaseDetectState(
    val selectedImageUri: Uri? = null,
    val selectedImageBitmap: Bitmap? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val diagnosisResult: String? = null,
    val diseaseDetails: com.appdev.smartkisan.domain.model.DiseaseResult? = null,
    // New state for the updated UI
    val showInstructionDialog: Boolean = false,
    val currentTab: Int = 0, // 0 for Crops, 1 for Fruits
    val selectedPlantItem: String? = null,

    val showCameraPermitRationale: Boolean = false,

    // New states for cropping functionality
    val originalImageBitmap: Bitmap? = null,
    val showCropper: Boolean = false,
    val processingImage: Boolean = false,
    val backgroundRemoved: Boolean = false,

    // Example plant items - replace these with your actual items
    val cropItems: List<PlantItem> = listOf(
        PlantItem("Rice", R.drawable.tempicon),
        PlantItem("Tomato", R.drawable.tomato),
        PlantItem("Corn", R.drawable.corn),
        PlantItem("Potato", R.drawable.potato)
    ),

    val fruitItems: List<PlantItem> = listOf(
        PlantItem("Apple", R.drawable.apple),
        PlantItem("Grapes", R.drawable.grapes),
        PlantItem("Orange", R.drawable.orange)
    ),
    val suggestedProducts: List<com.appdev.smartkisan.domain.model.Product> = emptyList(),
    val isLoadingProducts: Boolean = false,
    val productsError: String? = null
)
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String,
    val isHealthy: Boolean = false
)
data class DiseaseDetails(
    val causes: List<String>,
    val treatments: List<String>
)


data class PlantItem(val plantName:String,val drawable:Int)