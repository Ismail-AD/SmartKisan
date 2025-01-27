package com.appdev.smartkisan.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    @SerialName("id") var id: String? = null,
    @SerialName("name") var name: String = "",
    @SerialName("imageurl") var imageUrl: String = "",
    @SerialName("contact") var contact: String = "",
    @SerialName("role") var role: String = ""
)