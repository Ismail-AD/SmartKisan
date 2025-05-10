package com.appdev.smartkisan.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Product(
    var id: Long = 0L,
    var creatorId: String = "",
    var category: String = "",
    var name: String,
    var brandName: String = "",
    var price: Double,
    var discountPrice: Double = 0.0,
    var imageUrls: List<String> = listOf(),
    var description: String,
    var quantity: Long,
    var weightOrVolume: Float,
    var updateTime: String = "",
    var unit: String? = "",
    // Seeds specific attributes
    var germinationRate: Float? = null,
    var plantingSeason: List<String>? = null,
    var daysToHarvest: Long? = null,
    // Fertilizer specific attributes
    var applicationMethod: String? = null,
    // Medicine specific attributes
    var targetPestsOrDiseases: List<String>? = null
) : Parcelable

