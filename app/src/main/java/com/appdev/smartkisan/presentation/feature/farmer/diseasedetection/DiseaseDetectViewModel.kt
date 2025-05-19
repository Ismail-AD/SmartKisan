package com.appdev.smartkisan.presentation.feature.farmer.diseasedetection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.BuildConfig
import com.appdev.smartkisan.data.repository.Repository
import com.appdev.smartkisan.Utils.DiseaseDetailsProvider
import com.appdev.smartkisan.Utils.ResultState
import com.example.plantdisease.model.CropType
import com.example.plantdisease.model.classifyDisease
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class DiseaseDetectViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    private val TAG: String = "DISEASE"
    private val _detectUiState = MutableStateFlow(DiseaseDetectState())
    val detectUiState: StateFlow<DiseaseDetectState> = _detectUiState.asStateFlow()

    private val genAI by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_KEY
        )
    }

    fun onAction(action: DiseaseDetectActions) {
        when (action) {
            is DiseaseDetectActions.AddSelectedImage -> {
                _detectUiState.update { it.copy(selectedImageUri = action.uri) }
            }

            is DiseaseDetectActions.ClearData -> {
                _detectUiState.update {
                    it.copy(
                        diseaseDetails = null,
                        diagnosisResult = null,
                        showCropper = false,
                        backgroundRemoved = false,
                        isLoading = false
                    )
                }
            }

            is DiseaseDetectActions.GoBack -> {
                // Handled in composable
            }

            is DiseaseDetectActions.NavigateToResultScreen -> {
                // Handled in composable
            }

            is DiseaseDetectActions.StartDiagnosis -> {
                val uri = _detectUiState.value.selectedImageUri
                val bitmap = _detectUiState.value.selectedImageBitmap
                val cropType = getSelectedCropType(_detectUiState.value.selectedPlantItem)

                if (uri != null && cropType != null) {
                    _detectUiState.update {
                        it.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    bitmap?.let { analyzeImage(action.context, it, cropType) }
                } else {
                    _detectUiState.update {
                        it.copy(
                            error = "Please select both a crop type and an image",
                            isLoading = false
                        )
                    }
                }
            }

            is DiseaseDetectActions.FinishCropping -> {
                _detectUiState.update {
                    it.copy(
                        selectedImageBitmap = action.croppedBitmap,
                        showCropper = false
                    )
                }
            }

            is DiseaseDetectActions.ExtractedBitmap -> {
                _detectUiState.update { it.copy(selectedImageBitmap = action.bitmap) }
            }

            is DiseaseDetectActions.ClearValidationError -> {
                _detectUiState.update { it.copy(error = null) }
            }

            is DiseaseDetectActions.CurrentSelectedTab -> {
                _detectUiState.update {
                    it.copy(
                        currentTab = action.tabIndex,
                        // Reset selected plant item when changing tabs
                        selectedPlantItem = null
                    )
                }
            }

            is DiseaseDetectActions.SelectPlantItem -> {
                _detectUiState.update { it.copy(selectedPlantItem = action.itemName) }
            }

            is DiseaseDetectActions.ToggleInstructionDialog -> {
                _detectUiState.update { it.copy(showInstructionDialog = !it.showInstructionDialog) }
            }

            is DiseaseDetectActions.ValidationError -> {
                _detectUiState.update { it.copy(error = action.message) }
            }

            is DiseaseDetectActions.DismissCameraDialog -> {
                _detectUiState.update { it.copy(showCameraPermitRationale = false) }
            }

            is DiseaseDetectActions.CaptureOriginalImage -> {
                _detectUiState.update {
                    it.copy(
                        originalImageBitmap = action.bitmap,
                        showCropper = true
                    )
                }
            }

            is DiseaseDetectActions.StartCropping -> {
                _detectUiState.update {
                    it.copy(
                        originalImageBitmap = action.bitmap,
                        showCropper = true
                    )
                }
            }

            is DiseaseDetectActions.BackgroundRemovalStarted -> {
                _detectUiState.update { it.copy(processingImage = true) }
            }

            is DiseaseDetectActions.PermissionDeniedPermanent -> {
                _detectUiState.update { it.copy(showCameraPermitRationale = true) }
            }

            is DiseaseDetectActions.BackgroundRemovalFinished -> {
                _detectUiState.update {
                    it.copy(
                        selectedImageBitmap = action.processedBitmap,
                        processingImage = false,
                        backgroundRemoved = true
                    )
                }
            }

            is DiseaseDetectActions.BackgroundRemovalFailed -> {
                _detectUiState.update {
                    it.copy(
                        processingImage = false,
                        error = action.error
                    )
                }
            }

            is DiseaseDetectActions.FetchProductsForDisease -> {
                fetchProductsForDisease(action.diseaseName)
            }

            else -> {
                // Handle other actions
            }
        }
    }


    private fun fetchProductsForDisease(diseaseName: String) {
        _detectUiState.update { it.copy(isLoadingProducts = true) }

        viewModelScope.launch {
            repository.getMedicineProductsForDisease(diseaseName).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        // Already handled at the start of this function
                    }

                    is ResultState.Success -> {
                        _detectUiState.update {
                            it.copy(
                                suggestedProducts = result.data,
                                isLoadingProducts = false,
                                productsError = if (result.data.isEmpty()) "No specific products found for this disease" else null
                            )
                        }
                    }

                    is ResultState.Failure -> {
                        _detectUiState.update {
                            it.copy(
                                isLoadingProducts = false,
                                productsError = "Failed to load suggested products: ${result.msg.localizedMessage}"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun analyzeImage(context: Context, bitmap: Bitmap, cropType: CropType) {
        viewModelScope.launch {
            try {
                // 1. Validate with Gemini AI that this is a relevant plant leaf image
                val validationResult = validateImageWithGemini(context, bitmap, cropType)

                if (!validationResult.isValid) {
                    _detectUiState.update {
                        it.copy(
                            isLoading = false,
                            error = validationResult.errorMessage
                        )
                    }
                    return@launch
                }

                // Check if the leaf is healthy
                if (validationResult.isHealthy) {
                    // Skip disease classification for healthy leaves
                    _detectUiState.update {
                        it.copy(
                            isLoading = false,
                            diseaseDetails = com.appdev.smartkisan.domain.model.DiseaseResult(
                                diseaseName = "Healthy",
                                confidence = 100,
                                causedBy = listOf("No disease detected - the plant appears healthy"),
                                treatments = listOf("Continue regular plant care and maintenance")
                            )
                        )
                    }
                    return@launch
                }

                // 3. Perform disease classification using the TFLite model
                val classificationResult = classifyDisease(context, bitmap, cropType)

                if (classificationResult == null) {
                    _detectUiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to classify disease. Please try again with a clearer image."
                        )
                    }
                    return@launch
                }

                // 4. Get disease details based on the classification result
                val (diseaseName, confidence) = classificationResult
                val diseaseDetails = getDiseaseDetails(cropType, diseaseName)

                // 5. Update UI state with the result
                _detectUiState.update {
                    it.copy(
                        isLoading = false,
                        diseaseDetails = com.appdev.smartkisan.domain.model.DiseaseResult(
                            diseaseName = diseaseName,
                            confidence = (confidence * 100).toInt(),
                            causedBy = diseaseDetails.causes,
                            treatments = diseaseDetails.treatments
                        )
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error analyzing image: ${e.message}", e)
                _detectUiState.update {
                    it.copy(
                        isLoading = false,
                        error = "An error occurred: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private suspend fun validateImageWithGemini(
        context: Context,
        bitmap: Bitmap,
        cropType: CropType
    ): ValidationResult {
        return withContext(Dispatchers.IO) {
            try {
                // Convert bitmap to byte array for Gemini
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                // Get the list of possible diseases for this crop type
                val possibleDiseases =
                    DiseaseDetailsProvider.DISEASE_DETAILS_MAP[cropType]?.keys?.toList()
                        ?.filterNot { it == "Healthy" } ?: listOf()
                val diseasesString = possibleDiseases.joinToString(", ")
                val plantName = when (cropType) {
                    CropType.ORANGE -> "citrus"
                    else -> cropType.name.lowercase()
                }


                val prompt = """
     You are a plant disease detection expert. Please analyze this image and answer the following questions:
     1. Is this image showing a plant leaf? (Yes/No)
     2. Does this leaf appear to be from a $plantName plant? (Yes/No)
     3. Does the leaf show symptoms matching any of these specific diseases: $diseasesString? (Yes/No)
     4. Is this leaf healthy (no disease symptoms)? (Yes/No)
     
     If the leaf shows disease symptoms that do NOT match any of the listed diseases, answer "No" to question 3;
     if it's healthy, answer "Yes" to question 4.
     
     Please respond in JSON format only, with keys:
       - "isLeaf"
       - "isCorrectCrop"
       - "hasRecognizableDisease"
       - "isHealthy"
       - "reasoning"
     and nothing else.
 """.trimIndent()

                // Send to Gemini for analysis
                val response = genAI.generateContent(
                    content {
                        image(byteArray)
                        text(prompt)
                    }
                )

                val responseText = response.text?.trim() ?: ""
                Log.d(TAG, "Gemini response: $responseText")

                // Parse JSON response
                val jsonPattern = "\\{.*\\}".toRegex(RegexOption.DOT_MATCHES_ALL)
                val jsonMatch = jsonPattern.find(responseText)

                if (jsonMatch != null) {
                    try {
                        val jsonString = jsonMatch.value
                        val jsonObject = org.json.JSONObject(jsonString)

                        val isLeaf = when {
                            jsonObject.has("isLeaf") && jsonObject.get("isLeaf") is Boolean ->
                                jsonObject.getBoolean("isLeaf")

                            else ->
                                jsonObject.optString("isLeaf", "").equals("Yes", ignoreCase = true)
                        }

                        val isCorrectCrop = when {
                            jsonObject.has("isCorrectCrop") && jsonObject.get("isCorrectCrop") is Boolean ->
                                jsonObject.getBoolean("isCorrectCrop")

                            else ->
                                jsonObject.optString("isCorrectCrop", "")
                                    .equals("Yes", ignoreCase = true)
                        }

                        // Updated key name to better reflect what we're checking
                        val hasRecognizableDisease = when {
                            jsonObject.has("hasRecognizableDisease") && jsonObject.get("hasRecognizableDisease") is Boolean ->
                                jsonObject.getBoolean("hasRecognizableDisease")

                            else ->
                                jsonObject.optString("hasRecognizableDisease", "")
                                    .equals("Yes", ignoreCase = true)
                        }

                        val isHealthy = when {
                            jsonObject.has("isHealthy") && jsonObject.get("isHealthy") is Boolean ->
                                jsonObject.getBoolean("isHealthy")
                            else ->
                                jsonObject.optString("isHealthy", "")
                                    .equals("Yes", ignoreCase = true)
                        }

                        val reasoning = jsonObject.optString("reasoning", "")

                        // Validate based on the responses
                        return@withContext when {
                            !isLeaf -> ValidationResult(
                                isValid = false,
                                errorMessage = "The image does not appear to show a ${cropType.name.lowercase()} plant leaf.",
                                isHealthy = false
                            )

                            !isCorrectCrop -> ValidationResult(
                                isValid = false,
                                errorMessage = "The leaf in the image doesn't appear to be from a ${cropType.name.lowercase()} plant.",
                                isHealthy = false
                            )

                            !(hasRecognizableDisease || isHealthy) -> ValidationResult(
                                isValid = false,
                                errorMessage = "The model cannot classify this leaf: ${if (!hasRecognizableDisease) "disease not recognized" else ""}${if (!hasRecognizableDisease && !isHealthy) " and it's not healthy" else ""}.",
                                isHealthy = false
                            )

                            // Pass the isHealthy flag in the result
                            else -> ValidationResult(
                                isValid = true,
                                errorMessage = "",
                                isHealthy = isHealthy
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing Gemini JSON response: ${e.message}", e)
                        return@withContext ValidationResult(
                            isValid = false,
                            errorMessage = "Unable to verify the image. Please try again with a clearer image of a ${cropType.name.lowercase()} leaf.",
                            isHealthy = false
                        )
                    }
                } else {
                    return@withContext ValidationResult(
                        isValid = false,
                        errorMessage = "Unable to analyze the image. Please try again with a clearer image.",
                        isHealthy = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in Gemini validation: ${e.message}", e)
                return@withContext ValidationResult(
                    isValid = false,
                    errorMessage = "An error occurred during image analysis. Please try again.",
                    isHealthy = false
                )
            }
        }
    }

    /**
     * Converts the selected plant name to the corresponding Crop enum value
     * @param plantName The name of the selected plant
     * @return The corresponding Crop enum value, or null if not supported
     */
    private fun getSelectedCropType(plantName: String?): CropType? {
        return when (plantName?.lowercase()) {
            "apple" -> CropType.APPLE
            "corn", "maize" -> CropType.CORN
            "tomato" -> CropType.TOMATO
            "potato" -> CropType.POTATO
            "grapes" -> CropType.GRAPES
            "orange", "citrus" -> CropType.ORANGE
            "rice" -> CropType.RICE
            else -> null
        }
    }

    private fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                decoder.isMutableRequired = true
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    private fun getDiseaseDetails(cropType: CropType, diseaseName: String): DiseaseDetails {
        return DiseaseDetailsProvider.DISEASE_DETAILS_MAP[cropType]?.get(diseaseName)
            ?: DiseaseDetails(
                causes = listOf("Unknown causes for this disease"),
                treatments = listOf("Consult with a local agricultural expert")
            )
    }
}