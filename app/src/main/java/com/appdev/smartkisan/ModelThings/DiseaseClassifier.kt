package com.example.plantdisease.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

/**
 * Classifies plant diseases from a bitmap image using TensorFlow Lite model
 * @param context Application context to access resources
 * @param bitmap The bitmap image to classify
 * @param cropType The type of crop for which to detect disease
 * @return Pair of (disease name, confidence) or null if classification fails
 */
fun classifyDisease(context: Context, bitmap: Bitmap, cropType: CropType?): Pair<String, Float>? {
    val TAG = "PlantDiseaseClassifier"

    try {
        // Get model file and labels for the specified crop type
        val (modelFileName, classes) = getModelForCrop(cropType ?: CropType.RICE)

        // Model expects 224x224 RGB images
        val inputWidth = 224
        val inputHeight = 224
        val channels = 3

        // IMPORTANT FIX: Convert HARDWARE bitmap to a mutable, CPU-accessible bitmap
        // Create a copy that's not hardware accelerated
        val bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Preprocess bitmap
        val resized = Bitmap.createScaledBitmap(bitmapCopy, inputWidth, inputHeight, true)

        // Allocate ByteBuffer for input tensor
        val inputBuffer = ByteBuffer.allocateDirect(4 * inputWidth * inputHeight * channels)
        inputBuffer.order(ByteOrder.nativeOrder())

        // Normalize pixel values to [0, 1]
        for (y in 0 until inputHeight) {
            for (x in 0 until inputWidth) {
                val pixel = resized.getPixel(x, y)

                // Extract and normalize RGB values (scale from 0-255 to 0-1)
                inputBuffer.putFloat(((pixel shr 16 and 0xFF) / 255.0f)) // R
                inputBuffer.putFloat(((pixel shr 8 and 0xFF) / 255.0f))  // G
                inputBuffer.putFloat(((pixel and 0xFF) / 255.0f))        // B
            }
        }

        // Reset position to start for reading
        inputBuffer.rewind()

        // Load TFLite model
        val model = FileUtil.loadMappedFile(context, modelFileName)
        val interpreter = Interpreter(model)

        // Prepare output buffer
        val numClasses = classes.size
        val outputBuffer = Array(1) { FloatArray(numClasses) }

        // Run inference
        interpreter.run(inputBuffer, outputBuffer)

        // Find top prediction
        val confidences = outputBuffer[0]
        var maxIdx = 0
        var maxConfidence = confidences[0]

        for (i in 1 until numClasses) {
            if (confidences[i] > maxConfidence) {
                maxIdx = i
                maxConfidence = confidences[i]
            }
        }

        // Get readable disease name
        val rawLabel = classes[maxIdx]
        val readableLabel = formatDiseaseLabel(rawLabel)

        // Clean up resources
        interpreter.close()

        Log.d(TAG, "Classification result: $readableLabel with confidence: ${maxConfidence * 100}%")
        return Pair(readableLabel, maxConfidence)
    } catch (e: Exception) {
        Log.e(TAG, "Error classifying disease: ${e.message}")
        e.printStackTrace()
        return null
    }
}

/**
 * Returns model file and labels for each crop type
 */
private fun getModelForCrop(cropType: CropType): Pair<String, List<String>> {
    return when (cropType) {
        CropType.ORANGE -> Pair(
            "new_citrus_disease_model.tflite",
            listOf(
                "Citrus canker",
                "Citrus greening",
                "Citrus mealybugs",
                "Powdery mildew",
                "Spiny whitefly",
                "Healthy Leaf"
            )
        )

        CropType.GRAPES -> Pair(
            "grapes_disease_model.tflite",
            listOf(
                "Grape___Black_rot",
                "Grape___Esca_(Black_Measles)",
                "Grape___Leaf_blight_(Isariopsis_Leaf_Spot)",
                "Grape___healthy"
            )
        )

        CropType.APPLE -> Pair(
            "apple_disease_model.tflite",
            listOf(
                "Apple___Apple_scab",
                "Apple___Black_rot",
                "Apple___Cedar_apple_rust",
                "Apple___healthy"
            )
        )

        CropType.CORN -> Pair(
            "corp_corn_disease_model.tflite",
            listOf(
                "Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot",
                "Corn_(maize)___Common_rust_",
                "Corn_(maize)___Northern_Leaf_Blight",
                "Corn_(maize)___healthy"
            )
        )

        CropType.TOMATO -> Pair(
            "tomato_disease_model.tflite",
            listOf(
                "Tomato___Bacterial_spot",
                "Tomato___Early_blight",
                "Tomato___Late_blight",
                "Tomato___Leaf_Mold",
                "Tomato___Septoria_leaf_spot",
                "Tomato___Spider_mites Two-spotted_spider_mite",
                "Tomato___Target_Spot",
                "Tomato___Tomato_Yellow_Leaf_Curl_Virus",
                "Tomato___Tomato_mosaic_virus",
                "Tomato___healthy"
            )
        )

        CropType.POTATO -> Pair(
            "potato_disease_model.tflite",
            listOf(
                "Potato___Early_blight",
                "Potato___Late_blight",
                "Potato___healthy"
            )
        )

        CropType.RICE -> Pair(
            "new_rice_disease_model.tflite",
            listOf(
                "Bacterial blight",
                "Brown spot",
                "Leaf Blast",
                "Healthy Rice Leaf"
            )
        )
    }
}

/**
 * Formats the raw model output into readable disease names
 */
private fun formatDiseaseLabel(rawLabel: String): String {
    // Extract disease part by removing the crop prefix
    val diseasePart = when {
        rawLabel.startsWith("Grape___") -> rawLabel.replace("Grape___", "")
        rawLabel.startsWith("Apple___") -> rawLabel.replace("Apple___", "")
        rawLabel.startsWith("Corn_(maize)___") -> rawLabel.replace("Corn_(maize)___", "")
        rawLabel.startsWith("Tomato___") -> rawLabel.replace("Tomato___", "")
        rawLabel.startsWith("Potato___") -> rawLabel.replace("Potato___", "")
        else -> rawLabel // For Orange and Rice that don't use prefixes
    }

    // Format specific disease names nicely
    return when (diseasePart) {
        "healthy", "Healthy" -> "Healthy"
        "Black_rot" -> "Black Rot"
        "Esca_(Black_Measles)" -> "Esca (Black Measles)"
        "Leaf_blight_(Isariopsis_Leaf_Spot)" -> "Leaf Blight"
        "Bacterial blight" -> "Bacterial Blight"
        "Brown spot" -> "Brown Spot"
        "Leaf Blast" -> "Leaf Blast"
        else -> diseasePart.replace("_", " ")
    }
}

/**
 * Data class to hold classification results
 */
data class ClassificationResult(
    val diseaseName: String,
    val confidence: Float,
    val isUncertain: Boolean = false
) {
    val confidencePercent: Int
        get() = (confidence * 100).roundToInt()

    val isHealthy: Boolean
        get() = diseaseName.equals("Healthy", ignoreCase = true)
}

/**
 * Supported crop types
 */
enum class CropType {
    ORANGE,
    GRAPES,
    APPLE,
    CORN,
    TOMATO,
    POTATO,
    RICE
}