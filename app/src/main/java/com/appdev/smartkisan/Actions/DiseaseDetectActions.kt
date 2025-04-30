package com.appdev.smartkisan.Actions

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri

sealed interface DiseaseDetectActions {
    data object GoBack : DiseaseDetectActions
    data object ClearValidationError : DiseaseDetectActions
    data class AddSelectedImage(val uri: Uri) : DiseaseDetectActions
    data class ExtractedBitmap(val bitmap: Bitmap?) : DiseaseDetectActions
    data class StartDiagnosis(val context: Context) : DiseaseDetectActions
}