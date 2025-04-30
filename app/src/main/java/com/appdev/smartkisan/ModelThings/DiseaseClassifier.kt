package com.appdev.smartkisan.ModelThings

import android.content.Context
import android.graphics.Bitmap
import com.appdev.smartkisan.ml.Newmodel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
fun classifyDisease(context: Context, imageBitmap: Bitmap): String {
    val model = Newmodel.newInstance(context)

    val convertedBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true)

    // Resize image to 224x224
    val resizedBitmap = Bitmap.createScaledBitmap(convertedBitmap, 224, 224, true)
    val tensorImage = TensorImage(DataType.FLOAT32)
    tensorImage.load(resizedBitmap)


    // Get the ByteBuffer from TensorImage
    val byteBuffer = tensorImage.buffer

    // Create input tensor
    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
    inputFeature0.loadBuffer(byteBuffer)

    // Run model inference
    val outputs = model.process(inputFeature0)
    val outputFeature0 = outputs.outputFeature0AsTensorBuffer

    // Get predicted index
    val predictionArray = outputFeature0.floatArray
    val predictedIndex = predictionArray.indices.maxByOrNull { predictionArray[it] } ?: -1

    // Define the disease labels (make sure the order matches your model's training labels)
    val diseaseLabels = listOf(
        "Apple Scab", "Apple Black Rot", "Apple Cedar Rust", "Healthy Apple",
        "Healthy Blueberry", "Cherry Powdery Mildew", "Healthy Cherry",
        "Corn Cercospora Leaf Spot", "Corn Common Rust", "Corn Northern Leaf Blight", "Healthy Corn",
        "Grape Black Rot", "Grape Esca (Black Measles)", "Grape Leaf Blight", "Healthy Grape",
        "Orange Citrus Greening", "Peach Bacterial Spot", "Healthy Peach",
        "Bell Pepper Bacterial Spot", "Healthy Bell Pepper",
        "Potato Early Blight", "Potato Late Blight", "Healthy Potato",
        "Healthy Raspberry", "Healthy Soybean",
        "Squash Powdery Mildew", "Strawberry Leaf Scorch", "Healthy Strawberry",
        "Tomato Bacterial Spot", "Tomato Early Blight", "Tomato Late Blight",
        "Tomato Leaf Mold", "Tomato Septoria Leaf Spot", "Tomato Spider Mites",
        "Tomato Target Spot", "Tomato Yellow Leaf Curl Virus", "Tomato Mosaic Virus", "Healthy Tomato"
    )

    // Get predicted label
    val predictedLabel = if (predictedIndex in diseaseLabels.indices) diseaseLabels[predictedIndex] else "Unknown"

    // Release model resources
    model.close()

    return predictedLabel
}

// Helper function to convert bitmap to input tensor
private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
    val imgData = ByteBuffer.allocateDirect(1 * 224 * 224 * 3 * 4) // 4 bytes per float
    imgData.order(ByteOrder.nativeOrder())

    val pixels = IntArray(224 * 224)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

    for (i in 0 until 224) {
        for (j in 0 until 224) {
            val pixelValue = pixels[i * 224 + j]

            // Extract RGB values and normalize to [-1, 1] or [0, 1] depending on your model
            // This example uses [0, 1] normalization
            // Modify your preprocessing in Android
            imgData.putFloat(((pixelValue shr 16) and 0xFF) / 127.5f - 1.0f)
            imgData.putFloat(((pixelValue shr 8) and 0xFF) / 127.5f - 1.0f)
            imgData.putFloat((pixelValue and 0xFF) / 127.5f - 1.0f)
        }
    }

    imgData.rewind()
    return imgData
}