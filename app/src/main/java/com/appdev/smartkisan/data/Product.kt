package com.appdev.smartkisan.data

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    var id: Long = 0L,
    var creatorId: String = "",
    var category: String = "",
    var name: String,
    var price: Double,
    var discountPrice: Double = 0.0,
    var imageUrls: List<String> = listOf(),
    var ratings: Float = 0f,
    var reviewsCount: Long = 0L,
    var description: String,
    var quantity: Long,
    var weightOrVolume: Float,
    var updateTime: String = "",
    var unit: String? = ""
)
