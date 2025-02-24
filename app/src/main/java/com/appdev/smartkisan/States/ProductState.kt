package com.appdev.smartkisan.States

import android.net.Uri

data class ProductState(
    val productName: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val weight: Float = 0.0f,
    val quantity: Long = 0L,
    val measurement: String = "Kg",
    val measurements: List<String> = listOf("Kg", "g", "ml", "L"),
    val imageUris: List<Uri> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val uploaded:Boolean = false
)
