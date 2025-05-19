package com.appdev.smartkisan.presentation.feature.Seller.productmanagement

import android.net.Uri

data class ProductState(
    val pid: Long? = null,
    val productName: String = "",
    val price: String = "",
    val description: String = "",
    var brandName: String = "",
    val weight: String = "",
    val quantity: String = "",
    val measurement: String = "Kg",
    val measurements: List<String> = listOf("Kg", "g", "ml", "L"),
    val categories: List<String> = listOf("Seeds", "Fertilizers", "Medicine"),
    val selectedCategory: String = "Seeds",

    // New fields for category-specific attributes
    val applicationMethods: List<String> = listOf(
        "Spray",
        "Soil mix",
        "Foliar application",
        "Drip irrigation",
        "Broadcasting",
        "Seed treatment",
        "Drenching",
        "Furrow application",
        "Side dressing",
        "Top dressing"
    ),
    val selectedApplicationMethod: String = "Spray",

    val plantingSeasons: List<String> = listOf(
        "Spring",
        "Summer",
        "Autumn",
        "Winter",
        "Rainy",
        "Dry",
        "All Year"
    ),
    val selectedPlantingSeason: String = "Spring",

    // Disease handling for Medicine category
    val diseases: List<String> = listOf(""),

    // Existing fields
    val imageUris: List<Uri> = emptyList(),
    val initialUris: List<Uri> = emptyList(),
    val imageURLS: List<String> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val uploaded: Boolean = false
)
