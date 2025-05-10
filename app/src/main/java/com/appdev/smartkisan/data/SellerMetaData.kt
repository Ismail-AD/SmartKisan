package com.appdev.smartkisan.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SellerMetaData(
    @SerialName("id")
    val id: String = "",

    @SerialName("shopName")
    val shopName: String = "",

    @SerialName("contact")
    val contact: String = "",

    @SerialName("latitude")
    val latitude: Double = 0.0,

    @SerialName("longitude")
    val longitude: Double = 0.0
)