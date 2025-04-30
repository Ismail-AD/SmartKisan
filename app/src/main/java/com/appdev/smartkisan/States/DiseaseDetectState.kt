package com.appdev.smartkisan.States

import android.graphics.Bitmap
import android.net.Uri
import com.appdev.smartkisan.data.Product

data class DiseaseDetectState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedImageUri: Uri? = null,
    val selectedImageBitmap: Bitmap? = null,
    val diagnosisResult: String? = null
)
