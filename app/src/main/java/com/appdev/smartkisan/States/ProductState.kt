package com.appdev.smartkisan.States

import android.net.Uri

data class ProductState(
    val pid: Long? = null,
    val productName: String = "",
    val price: String = "",
    val description: String = "",
    val weight: String = "",
    val quantity: String = "",
    val measurement: String = "Kg",
    val measurements: List<String> = listOf("Kg", "g", "ml", "L"),
    val categories: List<String> = listOf("Seeds", "Fertilizers", "Medicine"),
    val selectedCategory: String = "Seeds",
    val imageUris: List<Uri> = emptyList(),
    val initialUris: List<Uri> = emptyList(),
    val imageURLS: List<String> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val uploaded: Boolean = false
)
