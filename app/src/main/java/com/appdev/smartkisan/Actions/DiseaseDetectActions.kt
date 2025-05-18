package com.appdev.smartkisan.Actions

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri

sealed interface DiseaseDetectActions {
    data object GoBack : DiseaseDetectActions
    data object NavigateToResultScreen : DiseaseDetectActions
    data object ClearValidationError : DiseaseDetectActions
    data object ClearData : DiseaseDetectActions
    data class AddSelectedImage(val uri: Uri) : DiseaseDetectActions
    data class ExtractedBitmap(val bitmap: Bitmap?) : DiseaseDetectActions
    data class StartDiagnosis(val context: Context) : DiseaseDetectActions

    data class CurrentSelectedTab(val tabIndex: Int) : DiseaseDetectActions
    data class SelectPlantItem(val itemName: String) : DiseaseDetectActions
    object ToggleInstructionDialog : DiseaseDetectActions
    data class ValidationError(val message: String) : DiseaseDetectActions

    data class CaptureOriginalImage(val bitmap: Bitmap) : DiseaseDetectActions
    data class StartCropping(val bitmap: Bitmap) : DiseaseDetectActions
    data class FinishCropping(val croppedBitmap: Bitmap) : DiseaseDetectActions
    object BackgroundRemovalStarted : DiseaseDetectActions
    data class BackgroundRemovalFinished(val processedBitmap: Bitmap) : DiseaseDetectActions
    data class BackgroundRemovalFailed(val error: String) : DiseaseDetectActions
    object OpenCamera : DiseaseDetectActions
    object PermissionDeniedPermanent : DiseaseDetectActions
    object DismissCameraDialog: DiseaseDetectActions
}