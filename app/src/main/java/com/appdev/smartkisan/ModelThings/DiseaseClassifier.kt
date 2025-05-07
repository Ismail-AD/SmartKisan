package com.appdev.smartkisan.ModelThings

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import java.nio.MappedByteBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.channels.FileChannel

fun classifyDisease(context: Context, bitmap: Bitmap): String {
    return try {
        val model = Interpreter(loadModelFile(context, "udpatedmodel.tflite"))

        val inputShape = model.getInputTensor(0).shape() // Should be [1, 160, 160, 3]
        val inputDataType = model.getInputTensor(0).dataType()
        Log.d("AZX", "Shape: ${inputShape.contentToString()}, Type: $inputDataType")

        val tensorImage = TensorImage(DataType.FLOAT32)

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 160, 160, true)
        val argbBitmap = resizedBitmap.copy(Bitmap.Config.ARGB_8888, true)

        tensorImage.load(argbBitmap)

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(160, 160, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(127.5f, 127.5f)) // [-1, 1] normalization
            .build()

        val processedImage = imageProcessor.process(tensorImage)

        val outputShape = model.getOutputTensor(0).shape()
        val outputDataType = model.getOutputTensor(0).dataType()
        val outputBuffer = TensorBuffer.createFixedSize(outputShape, outputDataType)


        model.run(processedImage.buffer, outputBuffer.buffer.rewind())

        val confidences = outputBuffer.floatArray
        val maxIndex = confidences.indices.maxByOrNull { confidences[it] } ?: -1

        val labels = listOf(
            "Apple___Apple_scab",
            "Apple___Black_rot",
            "Apple___Cedar_apple_rust",
            "Apple___healthy",
            "Background_without_leaves",
            "Blueberry___healthy",
            "Cherry___Powdery_mildew",
            "Cherry___healthy",
            "Corn___Cercospora_leaf_spot Gray_leaf_spot",
            "Corn___Common_rust",
            "Corn___Northern_Leaf_Blight",
            "Corn___healthy",
            "Grape___Black_rot",
            "Grape___Esca_(Black_Measles)",
            "Grape___Leaf_blight_(Isariopsis_Leaf_Spot)",
            "Grape___healthy",
            "Orange___Haunglongbing_(Citrus_greening)",
            "Peach___Bacterial_spot",
            "Peach___healthy",
            "Pepper,_bell___Bacterial_spot",
            "Pepper,_bell___healthy",
            "Potato___Early_blight",
            "Potato___Late_blight",
            "Potato___healthy",
            "Raspberry___healthy",
            "Soybean___healthy",
            "Squash___Powdery_mildew",
            "Strawberry___Leaf_scorch",
            "Strawberry___healthy",
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
        labels.forEachIndexed { index, label ->
            Log.d("AZX", "Label: $label, Confidence: ${confidences.getOrNull(index)}")
        }

        model.close()
        if (maxIndex in labels.indices) labels[maxIndex] else "Unknown"
    } catch (e: Exception) {
        Log.e("AutoMLModel", "Error: ${e.message}")
        "Error: ${e.message}"
    }
}


//fun classifyDisease(context: Context, bitmap: Bitmap): String {
//    return try {
//        val model = Interpreter(loadModelFile(context, "model.tflite"))
//        logModelInfo(model)
//        val tensorImage = TensorImage(DataType.UINT8)
//
//        val argbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//        tensorImage.load(argbBitmap)
//
//        val imageProcessor = ImageProcessor.Builder()
//            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR)) // Only resize
//            .build()
//
//        val processedImage = imageProcessor.process(tensorImage)
//
//        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 6), DataType.UINT8)
//
//
//        model.run(processedImage.buffer, outputBuffer.buffer.rewind())
//
//
//        val confidences = outputBuffer.floatArray
//
//        confidences.forEachIndexed { index, confidence ->
//            Log.d("Prediction", "Class $index: ${confidence}")
//        }
//
//
//        val maxIndex = confidences.indices.maxByOrNull { confidences[it] } ?: -1
//
//
//        val labels = listOf(
//            "Corn_common_rust",
//            "Corn_gray_leaf_spot",
//            "Potato_early_blight",
//            "Strawberry_leaf_scorch",
//            "Tomato_leaf_mold",
//            "Tomato_mosaic_virus"
//        )
//
//
//
//        model.close()
//        if (maxIndex in labels.indices) labels[maxIndex] else "Unknown"
//    } catch (e: Exception) {
//        Log.e("AutoMLModel", "Error: ${e.message}")
//        "Error: ${e.message}"
//    }
//}

private fun logModelInfo(interpreter: Interpreter) {
    try {
        val inputTensor = interpreter.getInputTensor(0)
        val outputTensor = interpreter.getOutputTensor(0)

        Log.d("ModelInfo", "Input shape: ${inputTensor.shape().joinToString()}")
        Log.d("ModelInfo", "Input type: ${inputTensor.dataType()}")
        Log.d("ModelInfo", "Output shape: ${outputTensor.shape().joinToString()}")
        Log.d("ModelInfo", "Output type: ${outputTensor.dataType()}")
    } catch (e: Exception) {
        Log.e("ModelInfo", "Failed to get model info: ${e.message}")
    }
}

@Throws(IOException::class)
private fun loadModelFile(context: Context, modelFilename: String): MappedByteBuffer {
    val fileDescriptor = context.assets.openFd(modelFilename)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
}